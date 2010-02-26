/*
 * IInArchive.h
 *
 *  Created on: Jan 16, 2010
 *      Author: boris
 */

#ifndef IINARCHIVE_H_
#define IINARCHIVE_H_

#include "JavaStaticInfo.h"

JT_BEGIN_CLASS("net/sf/sevenzipjbinding/impl", InArchiveImpl)
/*    */JT_FIELD(Long, bindingSession)
/*    */JT_FIELD(Long, sevenZipArchiveInstance)
/*    */JT_FIELD(Long, sevenZipArchiveInStreamInstance)
/*    */JT_CLASS_FINAL_METHOD(Void, setArchiveFormat, JT_STRING(archiveFormat,_))
JT_END_CLASS

#endif /* IINARCHIVE_H_ */
