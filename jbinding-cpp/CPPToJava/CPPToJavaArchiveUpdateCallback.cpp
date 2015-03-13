#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveUpdateCallback.h"
#include "CPPToJavaSequentialInStream.h"
#include "CPPToJavaInStream.h"
#include "UnicodeHelper.h"
#include "CodecTools.h"

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetUpdateItemInfo(UInt32 index, Int32 *newData, /*1 - new data, 0 - old data */
Int32 *newProperties, /* 1 - new properties, 0 - old properties */
UInt32 *indexInArchive /* -1 if there is no in archive, or if doesn't matter */
) {
    TRACE_OBJECT_CALL("GetUpdateItemInfo");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (newData) {
        if (_iArchiveUpdateCallback) {
            jboolean isNewData = _iArchiveUpdateCallback->isNewData(jniEnvInstance,
                    _javaImplementation, (jint) index);
            if (jniEnvInstance.exceptionCheck()) {
                return S_FALSE;
            }
            *newData = isNewData ? 1 : 0; // TODO Check, if this really helps
        } else {
            *newData = 1;
        }
    }

    if (newProperties) {
        if (_iArchiveUpdateCallback) {
            jboolean isNewProperties = _iArchiveUpdateCallback->isNewProperties(jniEnvInstance,
                    _javaImplementation, (jint) index);
            if (jniEnvInstance.exceptionCheck()) {
                return S_FALSE;
            }
            *newProperties = isNewProperties ? 1 : 0; // TODO Check, if this really helps
        } else {
            *newProperties = 1;
        }
    }

    if (indexInArchive) {
        if (_iArchiveUpdateCallback) {
            *indexInArchive = (UInt32) _iArchiveUpdateCallback->getOldArchiveItemIndex(
                    jniEnvInstance, _javaImplementation, (jint) index);
            if (jniEnvInstance.exceptionCheck()) {
                return S_FALSE;
            }
        } else {
            *indexInArchive = (UInt32) -1;
        }
    }

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetProperty(UInt32 index, PROPID propID,
                                                         PROPVARIANT *value) {

	#define JNI_TYPE_STRING                              jstring
	#define JNI_TYPE_INTEGER                             jobject
	#define JNI_TYPE_UINTEGER                            jobject
	#define JNI_TYPE_DATE                                jobject
	#define JNI_TYPE_BOOLEAN                             jboolean
	#define JNI_TYPE_LONG                                jlong

	#define ASSIGN_VALUE_TO_C_PROP_VARIANT_BOOLEAN       cPropVariant = (bool) value;
	#define ASSIGN_VALUE_TO_C_PROP_VARIANT_LONG          cPropVariant = (UInt64) value;

	#define ASSIGN_VALUE_TO_C_PROP_VARIANT_STRING                                                                       \
		if (value) {                                                                                                    \
			const jchar * jChars = jniEnvInstance->GetStringChars(value, NULL);                                         \
			cPropVariant = UString(UnicodeHelper(jChars));                                                              \
			jniEnvInstance->ReleaseStringChars(value, jChars);                                                          \
		}

	#define ASSIGN_VALUE_TO_C_PROP_VARIANT_INTEGER                                                                      \
		if (value) {                                                                                                    \
			cPropVariant = jni::Integer::intValue(jniEnvInstance, value);                                               \
			if (jniEnvInstance.exceptionCheck()) {                                                                      \
				return S_FALSE;                                                                                         \
			}                                                                                                           \
		}

    #define ASSIGN_VALUE_TO_C_PROP_VARIANT_UINTEGER                                                                     \
		if (value) {                                                                                                    \
			cPropVariant = (unsigned int)jni::Integer::intValue(jniEnvInstance, value);                                 \
			if (jniEnvInstance.exceptionCheck()) {                                                                      \
				return S_FALSE;                                                                                         \
			}                                                                                                           \
		}

	#define ASSIGN_VALUE_TO_C_PROP_VARIANT_DATE                                                                         \
		if (value) {                                                                                                    \
			FILETIME filetime;                                                                                          \
			if (!ObjectToFILETIME(jniEnvInstance, value, filetime)) {                                                   \
				return S_FALSE;                                                                                         \
			}                                                                                                           \
			cPropVariant = filetime;                                                                                    \
		}

	#define GET_ATTRIBUTE(TYPE, methodName)                                                                             \
	{                                                                                                                   \
		if (!_iOutItemCallback->_##methodName##_exists(jniEnvInstance)) {                                               \
			jniEnvInstance.reportError("IOutItemCallback implementation should implement " #methodName " method.");     \
			return S_FALSE;                                                                                             \
		}                                                                                                               \
		JNI_TYPE_##TYPE value = _iOutItemCallback->methodName(jniEnvInstance, _outItemCallbackImplementation);          \
		if (jniEnvInstance.exceptionCheck()) {                                                                          \
			return S_FALSE;                                                                                             \
		}                                                                                                               \
		ASSIGN_VALUE_TO_C_PROP_VARIANT_##TYPE                                                                           \
		break;                                                                                                          \
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
                    || codecTools.isBZip2Archive(_archiveFormatIndex))) {
        cPropVariant = false;
        cPropVariant.Detach(value);
        return S_OK;
    }

    if (propID == kpidTimeType) {
        cPropVariant = NFileTimeType::kWindows;
        cPropVariant.Detach(value);
        return S_OK;
    }

    if (_outItemCallbackLastIndex != index || _outItemCallbackImplementation == NULL) {

    	_outItemCallbackImplementation = _iOutCreateCallback->getOutItemCallback(jniEnvInstance, _javaImplementation, index);
		if (jniEnvInstance.exceptionCheck()) {
			return S_FALSE;
		}
		_iOutItemCallback = jni::IOutItemCallback::_getInstanceFromObject(jniEnvInstance, _outItemCallbackImplementation);

		_outItemCallbackLastIndex = index;
    }

    switch (propID) {
    case kpidAttrib:             GET_ATTRIBUTE(UINTEGER, getAttributes)
    case kpidPosixAttrib:        GET_ATTRIBUTE(UINTEGER, getPosixAttributes)
    case kpidPath:               GET_ATTRIBUTE(STRING,   getPath)
    case kpidIsDir:              GET_ATTRIBUTE(BOOLEAN,  isDir)
    case kpidIsAnti:             GET_ATTRIBUTE(BOOLEAN,  isAnti)
    case kpidTimeType:           GET_ATTRIBUTE(BOOLEAN,  isNtfsTime)
    case kpidMTime:              GET_ATTRIBUTE(DATE,     getModificationTime)
    case kpidATime:              GET_ATTRIBUTE(DATE,     getLastAccessTime)
    case kpidCTime:              GET_ATTRIBUTE(DATE,     getCreationTime)
    case kpidSize:               GET_ATTRIBUTE(LONG,     getSize)
    case kpidUser:               GET_ATTRIBUTE(STRING,   getUser)
    case kpidGroup:              GET_ATTRIBUTE(STRING,   getGroup)
    default:
#ifdef _DEBUG
    	printf("kpidNoProperty: %i\n", (int) kpidNoProperty);
    	printf("kpidMainSubfile: %i\n", (int) kpidMainSubfile);
    	printf("kpidHandlerItemIndex: %i\n", (int) kpidHandlerItemIndex);
    	printf("kpidPath: %i\n", (int) kpidPath);
    	printf("kpidName: %i\n", (int) kpidName);
    	printf("kpidExtension: %i\n", (int) kpidExtension);
    	printf("kpidIsDir: %i\n", (int) kpidIsDir);
    	printf("kpidSize: %i\n", (int) kpidSize);
    	printf("kpidPackSize: %i\n", (int) kpidPackSize);
    	printf("kpidAttrib: %i\n", (int) kpidAttrib);
    	printf("kpidCTime: %i\n", (int) kpidCTime);
    	printf("kpidATime: %i\n", (int) kpidATime);
    	printf("kpidMTime: %i\n", (int) kpidMTime);
    	printf("kpidSolid: %i\n", (int) kpidSolid);
    	printf("kpidCommented: %i\n", (int) kpidCommented);
    	printf("kpidEncrypted: %i\n", (int) kpidEncrypted);
    	printf("kpidSplitBefore: %i\n", (int) kpidSplitBefore);
    	printf("kpidSplitAfter: %i\n", (int) kpidSplitAfter);
    	printf("kpidDictionarySize: %i\n", (int) kpidDictionarySize);
    	printf("kpidCRC: %i\n", (int) kpidCRC);
    	printf("kpidType: %i\n", (int) kpidType);
    	printf("kpidIsAnti: %i\n", (int) kpidIsAnti);
    	printf("kpidMethod: %i\n", (int) kpidMethod);
    	printf("kpidHostOS: %i\n", (int) kpidHostOS);
    	printf("kpidFileSystem: %i\n", (int) kpidFileSystem);
    	printf("kpidUser: %i\n", (int) kpidUser);
    	printf("kpidGroup: %i\n", (int) kpidGroup);
    	printf("kpidBlock: %i\n", (int) kpidBlock);
    	printf("kpidComment: %i\n", (int) kpidComment);
    	printf("kpidPosition: %i\n", (int) kpidPosition);
    	printf("kpidPrefix: %i\n", (int) kpidPrefix);
    	printf("kpidNumSubDirs: %i\n", (int) kpidNumSubDirs);
    	printf("kpidNumSubFiles: %i\n", (int) kpidNumSubFiles);
    	printf("kpidUnpackVer: %i\n", (int) kpidUnpackVer);
    	printf("kpidVolume: %i\n", (int) kpidVolume);
    	printf("kpidIsVolume: %i\n", (int) kpidIsVolume);
    	printf("kpidOffset: %i\n", (int) kpidOffset);
    	printf("kpidLinks: %i\n", (int) kpidLinks);
    	printf("kpidNumBlocks: %i\n", (int) kpidNumBlocks);
    	printf("kpidNumVolumes: %i\n", (int) kpidNumVolumes);
    	printf("kpidTimeType: %i\n", (int) kpidTimeType);
    	printf("kpidBit64: %i\n", (int) kpidBit64);
    	printf("kpidBigEndian: %i\n", (int) kpidBigEndian);
    	printf("kpidCpu: %i\n", (int) kpidCpu);
    	printf("kpidPhySize: %i\n", (int) kpidPhySize);
    	printf("kpidHeadersSize: %i\n", (int) kpidHeadersSize);
    	printf("kpidChecksum: %i\n", (int) kpidChecksum);
    	printf("kpidCharacts: %i\n", (int) kpidCharacts);
    	printf("kpidVa: %i\n", (int) kpidVa);
    	printf("kpidId: %i\n", (int) kpidId);
    	printf("kpidShortName: %i\n", (int) kpidShortName);
    	printf("kpidCreatorApp: %i\n", (int) kpidCreatorApp);
    	printf("kpidSectorSize: %i\n", (int) kpidSectorSize);
    	printf("kpidPosixAttrib: %i\n", (int) kpidPosixAttrib);
    	printf("kpidLink: %i\n", (int) kpidLink);
    	printf("kpidTotalSize: %i\n", (int) kpidTotalSize);
    	printf("kpidFreeSpace: %i\n", (int) kpidFreeSpace);
    	printf("kpidClusterSize: %i\n", (int) kpidClusterSize);
    	printf("kpidVolumeName: %i\n", (int) kpidVolumeName);
    	printf("kpidLocalName: %i\n", (int) kpidLocalName);
    	printf("kpidProvider: %i\n", (int) kpidProvider);
    	printf("kpidUserDefined: %i\n", (int) kpidUserDefined);
#endif // _DEBUG

    	jniEnvInstance.reportError("CPPToJavaArchiveUpdateCallback::GetProperty() : unexpected propID=%u", propID);
    	return S_FALSE;
    }

    cPropVariant.Detach(value);

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetStream(UInt32 index, ISequentialInStream **inStream) {
    TRACE_OBJECT_CALL("GetStream");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (!inStream) {
        return S_OK;
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
    } else {
        return S_FALSE;
    }

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::SetOperationResult(Int32 operationResult) {
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
