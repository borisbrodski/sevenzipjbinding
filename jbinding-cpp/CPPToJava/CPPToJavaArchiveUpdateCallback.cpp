#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveUpdateCallback.h"
#include "CPPToJavaSequentialInStream.h"
#include "CPPToJavaInStream.h"
#include "UnicodeHelper.h"
#include "CodecTools.h"

#include "UserTrace.h"
void CPPToJavaArchiveUpdateCallback::freeOutItem(JNIEnvInstance & jniEnvInstance) {
    if (_outItem) {
        jniEnvInstance->DeleteGlobalRef(_outItem);
        _outItem = NULL;
    }
}

LONG CPPToJavaArchiveUpdateCallback::getOrUpdateOutItem(JNIEnvInstance & jniEnvInstance, int index) {
    if (_outItemLastIndex == index && _outItem) {
        return S_OK;
    }

    freeOutItem(jniEnvInstance);

    jobject outItemFactory = jni::OutItemFactory::newInstance(jniEnvInstance, _outArchive, index);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    jobject outItem = _iOutCreateCallback->getItemInformation(jniEnvInstance, _javaImplementation, index, outItemFactory);
    if (jniEnvInstance.exceptionCheck()) {
        jniEnvInstance->DeleteLocalRef(outItemFactory);
        return S_FALSE;
    }
    jniEnvInstance->DeleteLocalRef(outItemFactory);

    if (outItem == NULL) {
        jniEnvInstance.reportError("IOutCreateCallback.getItemInformation() should return "
                "a non-null reference to an item information object. Use outItemFactory to create an instance. "
                "Fill the new object with all necessary information about the archive item being processed.");
        return S_FALSE;
    }


    jni::OutItem::verify(jniEnvInstance, outItem, _isInArchiveAttached);
    if (jniEnvInstance.exceptionCheck()) {
        jniEnvInstance->DeleteLocalRef(outItem);
        return S_FALSE;
    }

    _outItem = jniEnvInstance->NewGlobalRef(outItem);
    jniEnvInstance->DeleteLocalRef(outItem);
    _outItemLastIndex = index;

    return S_OK;
}


STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetUpdateItemInfo(UInt32 index, Int32 *newData, /*1 - new data, 0 - old data */
Int32 *newProperties, /* 1 - new properties, 0 - old properties */
UInt32 *indexInArchive /* -1 if there is no in archive, or if doesn't matter */
) noexcept {
    TRACE_OBJECT_CALL("GetUpdateItemInfo");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    LONG result = getOrUpdateOutItem(jniEnvInstance, index);
    if (result) {
        return result;
    }

    UString traceMsg;
    bool isUserTrace = isUserTraceEnabled(jniEnvInstance, _outArchive);

    if (isUserTrace) {
        traceMsg += L"Get update info";
    }

    if (newData) {
        if (_isInArchiveAttached) {
            jobject newDataObject = jni::OutItem::updateIsNewData_Get(jniEnvInstance, _outItem);
            if (newDataObject) {
                *newData = jni::Boolean::booleanValue(jniEnvInstance, newDataObject);
                if (jniEnvInstance.exceptionCheck()) {
                    return S_FALSE;
                }
            } else {
                jniEnvInstance.reportError("The attribute 'updateNewData' of the corresponding IOutItem* class shouldn't be null (index=%i)", index);
                return S_FALSE;
            }
        } else {
            *newData = 1;
        }
        if (isUserTrace) {
            traceMsg += L" (new data: ";
            if (*newData) {
                traceMsg += L"true)";
            } else {
                traceMsg += L"false)";
            }
        }
    }

    if (newProperties) {
        if (_isInArchiveAttached) {
            jobject newPropertiesObject = jni::OutItem::updateIsNewProperties_Get(jniEnvInstance, _outItem);
            if (newPropertiesObject) {
                *newProperties = jni::Boolean::booleanValue(jniEnvInstance, newPropertiesObject);
                if (jniEnvInstance.exceptionCheck()) {
                    return S_FALSE;
                }
            } else {
                jniEnvInstance.reportError("The attribute 'updateNewProperties' of the corresponding IOutItem* class shouldn't be null (index=%i)", index);
                return S_FALSE;
            }
        } else {
            *newProperties = 1;
        }
        if (isUserTrace) {
            traceMsg += L" (new props: ";
            if (*newProperties) {
                traceMsg += L"true)";
            } else {
                traceMsg += L"false)";
            }
        }
    }

    if (indexInArchive) {
        if (_isInArchiveAttached) {
            jobject oldArchiveItemIndexObject = jni::OutItem::updateOldArchiveItemIndex_Get(jniEnvInstance, _outItem);
            if (oldArchiveItemIndexObject) {
                *indexInArchive = (UInt32) jni::Integer::intValue(jniEnvInstance, oldArchiveItemIndexObject);
                if (jniEnvInstance.exceptionCheck()) {
                    return S_FALSE;
                }
            } else {
                *indexInArchive = (UInt32) -1;
            }
        } else {
            *indexInArchive = (UInt32) -1;
        }
        if (isUserTrace) {
            traceMsg += UString(L" (old index: ") << (Int32)*indexInArchive << L")";
        }
    }

    if (isUserTrace) {
        traceMsg += UString(L" (index: ") << index << L")";
        userTrace(jniEnvInstance, _outArchive, traceMsg);
    }

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetProperty(UInt32 index, PROPID propID,
                                                         PROPVARIANT *value) noexcept {

//	#define JNI_TYPE_STRING                              jstring
//	#define JNI_TYPE_INTEGER                             jobject
//	#define JNI_TYPE_UINTEGER                            jobject
//	#define JNI_TYPE_DATE                                jobject
//	#define JNI_TYPE_BOOLEAN                             jboolean
//	#define JNI_TYPE_LONG                                jlong

	#define ASSIGN_VALUE_TO_C_PROP_VARIANT_STRING                                                                   \
        cPropVariant = UString(FromJChar(jniEnvInstance, (jstring)jvalue));

	#define ASSIGN_VALUE_TO_C_PROP_VARIANT_INTEGER                                                                  \
        cPropVariant = (Int32)jni::Integer::intValue(jniEnvInstance, jvalue);                                       \
        if (jniEnvInstance.exceptionCheck()) {                                                                      \
            return S_FALSE;                                                                                         \
        }

    #define ASSIGN_VALUE_TO_C_PROP_VARIANT_BOOLEAN                                                                  \
        cPropVariant = (bool)jni::Boolean::booleanValue(jniEnvInstance, jvalue);                                    \
        if (jniEnvInstance.exceptionCheck()) {                                                                      \
            return S_FALSE;                                                                                         \
        }

    #define ASSIGN_VALUE_TO_C_PROP_VARIANT_LONG                                                                     \
        cPropVariant = (UInt64)jni::Long::longValue(jniEnvInstance, jvalue);                                        \
        if (jniEnvInstance.exceptionCheck()) {                                                                      \
            return S_FALSE;                                                                                         \
        }                                                                                                           \

    #define ASSIGN_VALUE_TO_C_PROP_VARIANT_UINTEGER                                                                 \
        cPropVariant = (UInt32)jni::Integer::intValue(jniEnvInstance, jvalue);                                      \
        if (jniEnvInstance.exceptionCheck()) {                                                                      \
            return S_FALSE;                                                                                         \
        }                                                                                                           \

	#define ASSIGN_VALUE_TO_C_PROP_VARIANT_DATE                                                                     \
        FILETIME filetime;                                                                                          \
        if (!ObjectToFILETIME(jniEnvInstance, jvalue, filetime)) {                                                  \
            return S_FALSE;                                                                                         \
        }                                                                                                           \
        cPropVariant = filetime;                                                                                    \

	#define GET_ATTRIBUTE(TYPE, fieldName)                                                                          \
	{                                                                                                               \
        if (isUserTraceEnabled(jniEnvInstance, _outArchive)) {                                                      \
            userTrace(jniEnvInstance, _outArchive,                                                                  \
                UString(L"Get property '" #fieldName "' (index: ") << index << L")");                               \
        }                                                                                                           \
        jobject jvalue = jni::OutItem::fieldName##_Get(jniEnvInstance, _outItem);                                   \
	    if (jvalue) {                                                                                                \
            ASSIGN_VALUE_TO_C_PROP_VARIANT_##TYPE                                                                   \
            jniEnvInstance->DeleteLocalRef(jvalue);                                                                 \
        } else {                                                                                                    \
            /* Field is null in Java - return VT_EMPTY for missing property */                                      \
            value->vt = VT_EMPTY;                                                                                   \
            return S_OK;                                                                                            \
        }                                                                                                           \
		break;                                                                                                      \
	}

    TRACE_OBJECT_CALL("GetProperty");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (!value) {
        return S_OK;
    }

    value->vt = VT_NULL;
    NWindows::NCOM::CPropVariant cPropVariant;

    if (propID == kpidIsDir
            && (codecTools.isGZipArchive(_archiveFormatIndex)
                    || codecTools.isBZip2Archive(_archiveFormatIndex)
                    || codecTools.isXzArchive(_archiveFormatIndex))) {
        cPropVariant = false;
        cPropVariant.Detach(value);
        return S_OK;
    }

    if (propID == kpidTimeType) {
        cPropVariant = (UInt32)NFileTimeType::kWindows;
        cPropVariant.Detach(value);
        return S_OK;
    }

    LONG result = getOrUpdateOutItem(jniEnvInstance, index);
    if (result) {
        return result;
    }

   switch (propID) {
    case kpidSize:               GET_ATTRIBUTE(LONG,     dataSize)

    case kpidAttrib:             GET_ATTRIBUTE(UINTEGER, propertyAttributes)
    case kpidPosixAttrib:        GET_ATTRIBUTE(UINTEGER, propertyPosixAttributes)
    case kpidPath:               GET_ATTRIBUTE(STRING,   propertyPath)
    case kpidName: {
        // For tar and other formats, kpidName is often requested. 
        // Return the filename (without path) from propertyPath.
        if (isUserTraceEnabled(jniEnvInstance, _outArchive)) {
            userTrace(jniEnvInstance, _outArchive,
                UString(L"Get property 'kpidName' (index: ") << index << L")");
        }
        jobject pathValue = jni::OutItem::propertyPath_Get(jniEnvInstance, _outItem);
        if (pathValue) {
            jstring pathString = (jstring)pathValue;
            const jchar* pathChars = jniEnvInstance->GetStringChars(pathString, NULL);
            size_t pathLen = jniEnvInstance->GetStringLength(pathString);
            
            // Find last slash to extract filename
            int lastSlash = -1;
            for (int i = 0; i < (int)pathLen; i++) {
                if (pathChars[i] == '/' || pathChars[i] == '\\') {
                    lastSlash = i;
                }
            }
            
            if (lastSlash >= 0 && lastSlash < (int)pathLen - 1) {
                // Extract substring after last slash
                jstring nameString = jniEnvInstance->NewString(pathChars + lastSlash + 1, pathLen - lastSlash - 1);
                cPropVariant = UString(FromJChar(jniEnvInstance, nameString));
                jniEnvInstance->DeleteLocalRef(nameString);
            } else if (lastSlash < 0) {
                // No slash - the whole path is the filename
                cPropVariant = UString(FromJChar(jniEnvInstance, pathString));
            }
            jniEnvInstance->ReleaseStringChars(pathString, pathChars);
            jniEnvInstance->DeleteLocalRef(pathValue);
        } else {
            // propertyPath is null - return VT_EMPTY
            value->vt = VT_EMPTY;
            return S_OK;
        }
        break;
    }
    case kpidIsDir:              GET_ATTRIBUTE(BOOLEAN,  propertyIsDir)
    case kpidIsAnti:             GET_ATTRIBUTE(BOOLEAN,  propertyIsAnti)
    case kpidComment:            GET_ATTRIBUTE(STRING,   propertyComment)
    case kpidEncrypted:          GET_ATTRIBUTE(BOOLEAN,  propertyEncrypted)
    case kpidCRC:                GET_ATTRIBUTE(UINTEGER, propertyCRC)
    case kpidMTime:              GET_ATTRIBUTE(DATE,     propertyLastModificationTime)
    case kpidATime:              GET_ATTRIBUTE(DATE,     propertyLastAccessTime)
    case kpidCTime:              GET_ATTRIBUTE(DATE,     propertyCreationTime)
    case kpidUser:               GET_ATTRIBUTE(STRING,   propertyUser)
    case kpidGroup:              GET_ATTRIBUTE(STRING,   propertyGroup)
    case kpidSymLink:            GET_ATTRIBUTE(STRING,   propertySymLink)
    case kpidHardLink:           GET_ATTRIBUTE(STRING,   propertyHardLink)

   case kpidTimeType: // Should be processed by now
    default:
        // For unknown/unsupported properties, return VT_EMPTY instead of VT_NULL.
        // 7-zip handlers (especially tar) expect VT_EMPTY for missing properties.
        value->vt = VT_EMPTY;
        return S_OK;
    }

    cPropVariant.Detach(value);

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetStream(UInt32 index, ISequentialInStream **inStream) noexcept {
    TRACE_OBJECT_CALL("GetStream");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (!inStream) {
        return S_OK;
    }

    if (isUserTraceEnabled(jniEnvInstance, _outArchive)) {
        userTrace(jniEnvInstance, _outArchive,
            UString(L"Get stream (index: ") << index << L")");
    }

    jobject inStreamImpl = _iOutCreateCallback->getStream(jniEnvInstance, _javaImplementation,
            (jint) index);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    if (inStreamImpl) {

        jclass inStreamInterface = jniEnvInstance->FindClass(INSTREAM_CLASS);
        FATALIF(!inStreamInterface, "Class " INSTREAM_CLASS " not found");

        if (jniEnvInstance->IsInstanceOf(inStreamImpl, inStreamInterface)) {
            CPPToJavaInStream * newInStream = new CPPToJavaInStream(_jbindingSession, jniEnvInstance, inStreamImpl);
            CMyComPtr<IInStream> inStreamComPtr = newInStream;
            *inStream = inStreamComPtr.Detach();
        } else {
            CPPToJavaSequentialInStream * newInStream = new CPPToJavaSequentialInStream(
                    _jbindingSession, jniEnvInstance, inStreamImpl);
            CMyComPtr<ISequentialInStream> inStreamComPtr = newInStream;
            *inStream = inStreamComPtr.Detach();
        }
        jniEnvInstance->DeleteLocalRef(inStreamImpl);
    }

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::SetOperationResult(Int32 operationResult) noexcept {
    TRACE_OBJECT_CALL("SetOperationResult");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    jboolean operationResultBoolean = (operationResult == NArchive::NUpdate::NOperationResult::kOK);

    _iOutCreateCallback->setOperationResult(jniEnvInstance, _javaImplementation,
            operationResultBoolean);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::CryptoGetTextPassword(BSTR *password) noexcept {
    TRACE_OBJECT_CALL("CryptoGetTextPassword");
	Int32 passwordIsDefined;
	CryptoGetTextPassword2(&passwordIsDefined, password);
	if (!passwordIsDefined) {
		*password = NULL;
	}
    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::CryptoGetTextPassword2(Int32 *passwordIsDefined, BSTR *password) noexcept {
    TRACE_OBJECT_CALL("CryptoGetTextPassword");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (!_isICryptoGetTextPasswordImplemented) {
        if (passwordIsDefined) {
            *passwordIsDefined = false;
        }
        if (password) {
            *password = NULL;
        }
        return S_OK;
    }

	if (!_cryptoGetTextPassword) {
		_cryptoGetTextPassword = jni::ICryptoGetTextPassword::_getInstanceFromObject(jniEnvInstance, _javaImplementation);
	}

	jstring passwordString = _cryptoGetTextPassword->cryptoGetTextPassword(jniEnvInstance, _javaImplementation);

    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

	if (!passwordString)
	{
        if (passwordIsDefined)
        	*passwordIsDefined = false;
        if (password)
            *password = NULL;
        return S_OK;
	}

	if (passwordIsDefined)
		*passwordIsDefined = true;

    if (password)
        StringToBstr(UString(FromJChar(jniEnvInstance, passwordString)), password);

    if (passwordString)
        jniEnvInstance->DeleteLocalRef(passwordString);

	return S_OK;
}
