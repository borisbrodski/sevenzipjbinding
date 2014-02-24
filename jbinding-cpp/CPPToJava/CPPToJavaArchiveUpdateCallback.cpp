#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveUpdateCallback.h"
#include "CPPToJavaSequentialInStream.h"
#include "CPPToJavaInStream.h"
#include "UnicodeHelper.h"

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
    TRACE_OBJECT_CALL("GetProperty");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (!value) {
        return S_OK;
    }

    value->vt = VT_NULL;
    NWindows::NCOM::CPropVariant cPropVariant;

    switch (propID) {
    case kpidAttrib: {
		jobject integer = _iOutItemCallback->getAttributes(jniEnvInstance, _outItemCallbackImplementation, index);
		if (jniEnvInstance.exceptionCheck()) {
			return S_FALSE;
		}
		if (integer) {
			cPropVariant = jni::Integer::intValue(jniEnvInstance, integer);
		}
		break;
    }
    case kpidPosixAttrib: {
		jobject integer = _iOutItemCallback->getPosixAttributes(jniEnvInstance, _outItemCallbackImplementation, index);
		if (jniEnvInstance.exceptionCheck()) {
			return S_FALSE;
		}
		if (integer) {
			cPropVariant = jni::Integer::intValue(jniEnvInstance, integer);
		}
		break;
    }
    case kpidPath: {
		jstring string = _iOutItemCallback->getPath(jniEnvInstance, _outItemCallbackImplementation, index);
	    if (jniEnvInstance.exceptionCheck()) {
	        return S_FALSE;
	    }
	    if (string) {
			const jchar * jChars = jniEnvInstance->GetStringChars(string, NULL);
			cPropVariant = UString(UnicodeHelper(jChars));
			jniEnvInstance->ReleaseStringChars(string, jChars);
	    }
        break;
    }
    case kpidIsDir: {
		jboolean isDir = _iOutItemCallback->isDir(jniEnvInstance, _outItemCallbackImplementation, index);
	    if (jniEnvInstance.exceptionCheck()) {
	        return S_FALSE;
	    }
		cPropVariant = (bool) isDir;
		break;
    }
    case kpidIsAnti: {
		jboolean isAnti = _iOutItemCallback->isAnti(jniEnvInstance, _outItemCallbackImplementation, index);
	    if (jniEnvInstance.exceptionCheck()) {
	        return S_FALSE;
	    }
		cPropVariant = (bool) isAnti;
		break;
    }
    case kpidTimeType: {
		jboolean isNtfsTime = _iOutItemCallback->isNtfsTime(jniEnvInstance, _outItemCallbackImplementation, index);
	    if (jniEnvInstance.exceptionCheck()) {
	        return S_FALSE;
	    }
    	cPropVariant = (unsigned int)(isNtfsTime ? NFileTimeType::kWindows : NFileTimeType::kUnix);
    	break;
    }
    case kpidMTime: {
		jobject mtime = _iOutItemCallback->getModificationTime(jniEnvInstance, _outItemCallbackImplementation, index);
	    if (jniEnvInstance.exceptionCheck()) {
	        return S_FALSE;
	    }
	    if (mtime) {
			FILETIME filetime;
			ObjectToFILETIME(jniEnvInstance, mtime, filetime);
			cPropVariant = filetime;
	    }
    	break;
    }
    case kpidATime: {
		jobject atime = _iOutItemCallback->getLastAccessTime(jniEnvInstance, _outItemCallbackImplementation, index);
	    if (jniEnvInstance.exceptionCheck()) {
	        return S_FALSE;
	    }
		if (atime) {
			FILETIME filetime;
			ObjectToFILETIME(jniEnvInstance, atime, filetime);
			cPropVariant = filetime;
		}
    	break;
    }
    case kpidCTime: {
		jobject ctime = _iOutItemCallback->getCreationTime(jniEnvInstance, _outItemCallbackImplementation, index);
	    if (jniEnvInstance.exceptionCheck()) {
	        return S_FALSE;
	    }
		if (ctime) {
			FILETIME filetime;
			ObjectToFILETIME(jniEnvInstance, ctime, filetime);
			cPropVariant = filetime;
		}
    	break;
    }
    case kpidSize: {
		jlong size = _iOutItemCallback->getSize(jniEnvInstance, _outItemCallbackImplementation, index);
	    if (jniEnvInstance.exceptionCheck()) {
	        return S_FALSE;
	    }
    	cPropVariant = (UInt64)size;
    	break;
    }

    default:

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


    	jniEnvInstance.reportError("CPPToJavaArchiveUpdateCallback::GetProperty() : unexpected propID=%u", propID);
    	return S_FALSE;
    }

    cPropVariant.Detach(value);

//    // public Object getProperty(int index, PropID propID);
//    jobject result = _iArchiveUpdateCallback.getProperty(jniEnvInstance, _javaImplementation,
//            (jint) index, propIDObject);
//    if (jniEnvInstance.exceptionCheck()) {
//        return S_FALSE;
//    }
//
    if (0) {
    	// TODO remove this
    	// ObjectToPropVariant(jniEnvInstance, result, value);
    }

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetStream(UInt32 index, ISequentialInStream **inStream) {
    TRACE_OBJECT_CALL("GetStream");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (!inStream) {
        return S_OK;
    }

    jobject inStreamImpl = _iArchiveCreateCallback.getStream(jniEnvInstance, _javaImplementation,
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

    _iArchiveCreateCallback.setOperationResult(jniEnvInstance, _javaImplementation,
            operationResultBoolean);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    return S_OK;
}
