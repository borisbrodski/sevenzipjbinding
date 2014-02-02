#ifndef JAVAPACKAGESEVENZIP_H_
#define JAVAPACKAGESEVENZIP_H_

#include "JavaStaticInfo.h"

#define JT_PROP_ID(name, param_spec)    JT_PARAM(Object, "Lnet/sf/sevenzipjbinding/PropID;", name, param_spec) // TODO Extract signature
#define JT_EXTRACT_ASK_MODE(name, param_spec)   JT_PARAM(Object, "Lnet/sf/sevenzipjbinding/ExtractAskMode;", name, param_spec) // TODO Extract signature
#define JT_EXTRACT_OPERATION_RESULT(name, param_spec)   JT_PARAM(Object, "Lnet/sf/sevenzipjbinding/ExtractOperationResult;", name, param_spec) // TODO Extract signature
JT_BEGIN_INTERFACE(ISequentialInStream)
//      public int read(byte[] data)
/*    */JT_INTERFACE_METHOD(Int, read, JT_BYTE_ARRAY(data,_))
JT_END_CLASS

JT_BEGIN_INTERFACE(IArchiveOpenCallback)
//      public void setTotal(Long files, Long bytes)
/*    */JT_INTERFACE_METHOD(Void, setTotal, JT_LONG_OBJECT(files, JT_LONG_OBJECT(bytes, _)))

//      public void setCompleted(Long files, Long bytes)
/*    */JT_INTERFACE_METHOD(Void, setCompleted, JT_LONG_OBJECT(files, JT_LONG_OBJECT(bytes, _)))
JT_END_INTERFACE

JT_BEGIN_INTERFACE(IArchiveOpenVolumeCallback)
//      public Object getProperty(PropID propId)
/*    */JT_INTERFACE_METHOD(Object, getProperty, JT_PROP_ID(propId, _))

//      public IInStream getStream(String filename)
/*    */JT_INTERFACE_METHOD_OBJECT("Lnet/sf/sevenzipjbinding/IInStream;", // TODO Extract signature
        /*        */getStream, JT_STRING(filename, _))
JT_END_INTERFACE

JT_BEGIN_INTERFACE(ICryptoGetTextPassword)
//      public String cryptoGetTextPassword()
/*    */JT_INTERFACE_METHOD(String, cryptoGetTextPassword, _)
JT_END_INTERFACE

JT_BEGIN_INTERFACE(IProgress)
//      public void setTotal(long total)
/*    */JT_INTERFACE_METHOD(Void, setTotal, JT_LONG(total, _))

//      public void setCompleted(long completeValue)
/*    */JT_INTERFACE_METHOD(Void, setCompleted, JT_LONG(completeValue, _))
JT_END_INTERFACE

JT_BEGIN_INTERFACE(IArchiveExtractCallback)
//      public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode)
/*    */JT_INTERFACE_METHOD_OBJECT("Lnet/sf/sevenzipjbinding/ISequentialOutStream;", // TODO Extract signature
        /*        */getStream, JT_INT(index, JT_EXTRACT_ASK_MODE(extractAskMode, _)))

//      public void prepareOperation(ExtractAskMode extractAskMode)
/*    */JT_INTERFACE_METHOD(Void, prepareOperation, JT_EXTRACT_ASK_MODE(extractAskMode, _))

//      public void setOperationResult(ExtractOperationResult extractOperationResult)
/*    */JT_INTERFACE_METHOD(Void, setOperationResult, JT_EXTRACT_OPERATION_RESULT(extractOperationResult, _))
JT_END_INTERFACE

JT_BEGIN_INTERFACE(ISequentialOutStream)
//      public int write(byte[] data)
/*    */JT_INTERFACE_METHOD(Int, write, JT_BYTE_ARRAY(data, _))
JT_END_INTERFACE

JT_BEGIN_INTERFACE(IOutStream)
//      public void setSize(long newSize)
/*    */JT_INTERFACE_METHOD(Void, setSize, JT_LONG(newSize, _))
JT_END_INTERFACE

JT_BEGIN_INTERFACE(ISeekableStream)
//      public long seek(long offset, int seekOrigin) throws SevenZipException;
/*    */JT_INTERFACE_METHOD(Long, seek, JT_LONG(offset, JT_INT(seekOrigin, _)))
JT_END_INTERFACE

JT_BEGIN_INTERFACE(IArchiveUpdateCallback)
//      public boolean isNewData(int index);
/*    */JT_INTERFACE_METHOD(Boolean, isNewData, JT_INT(index, _))

//      public boolean isNewProperties(int index);
/*    */JT_INTERFACE_METHOD(Boolean, isNewProperties, JT_INT(index, _))

//      public int getOldArchiveItemIndex(int index);
/*    */JT_INTERFACE_METHOD(Int, getOldArchiveItemIndex, JT_INT(index, _))

//      public Object getProperty(int index, PropID propID);
/*    */JT_INTERFACE_METHOD(Object, getProperty, JT_INT(index, JT_PROP_ID(propId, _)))

//      public ISequentialInStream getStream(int index);
/*    */JT_INTERFACE_METHOD_OBJECT("Lnet/sf/sevenzipjbinding/ISequentialInStream;", // TODO Extract signature
        /*        */getStream, JT_INT(index, _))

//      public void setOperationResult(boolean operationResultOk);
/*    */JT_INTERFACE_METHOD(Void, setOperationResult, JT_BOOLEAN(operationResultOk, _))
JT_END_INTERFACE

JT_BEGIN_CLASS("net/sf/sevenzipjbinding/impl", InArchiveImpl)
/*    */JT_FIELD(Long, jbindingSession)
/*    */JT_FIELD(Long, sevenZipArchiveInstance)
/*    */JT_FIELD(Long, sevenZipInStreamInstance)
/*    */JT_CLASS_FINAL_METHOD(Void, setArchiveFormat, JT_STRING(archiveFormat,_))
JT_END_CLASS

JT_BEGIN_CLASS("net/sf/sevenzipjbinding/impl", OutArchiveImpl)
/*    */JT_FIELD(Long, jbindingSession)
/*    */JT_FIELD(Long, sevenZipArchiveInstance)
/*    */JT_FIELD_OBJECT(archiveFormat, "Lnet/sf/sevenzipjbinding/ArchiveFormat;")
/*    */JT_FIELD(Int, archiveFormatIndex)
JT_END_CLASS


JT_BEGIN_CLASS("net/sf/sevenzipjbinding", PropertyInfo)
/*    */JT_FIELD(String, name)
/*    */JT_FIELD_OBJECT(propID, "Lnet/sf/sevenzipjbinding/PropID;")
/*    */JT_FIELD(Class, varType)
JT_END_CLASS

JT_BEGIN_CLASS("net/sf/sevenzipjbinding", PropID)
//      public static PropID getPropIDByIndex(int propIDIndex)
/*    */JT_CLASS_STATIC_METHOD_OBJECT("Lnet/sf/sevenzipjbinding/PropID;", getPropIDByIndex, JT_INT(propIDIndex, _)) // TODO Extract signature
JT_END_CLASS

JT_BEGIN_CLASS("net/sf/sevenzipjbinding", ExtractAskMode)
//      public static ExtractAskMode getExtractAskModeByIndex(int index)
/*    */JT_CLASS_STATIC_METHOD_OBJECT("Lnet/sf/sevenzipjbinding/ExtractAskMode;", getExtractAskModeByIndex, JT_INT(index, _)) // TODO Extract signature
JT_END_CLASS

JT_BEGIN_CLASS("net/sf/sevenzipjbinding", ExtractOperationResult)
//      public static ExtractOperationResult getOperationResult(int index)
/*    */JT_CLASS_STATIC_METHOD_OBJECT("Lnet/sf/sevenzipjbinding/ExtractOperationResult;", getOperationResult, JT_INT(index, _)) // TODO Extract signature
JT_END_CLASS

JT_BEGIN_CLASS("net/sf/sevenzipjbinding", ArchiveFormat)
/*    */JT_FIELD(Int, codecIndex)
/*    */JT_FIELD(String, methodName)
JT_END_CLASS

#endif /* JAVAPACKAGESEVENZIP_H_ */
