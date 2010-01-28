/*
 * IInArchive.h
 *
 *  Created on: Jan 16, 2010
 *      Author: boris
 */

#ifndef IINARCHIVE_H_
#define IINARCHIVE_H_

#include "JavaStaticInfo.h"

BEGIN_JCLASS("net/sf/sevenzipjbinding/impl", InArchiveImpl)
/*    */JCLASS_FIELD(Long, bindingSession)
/*    */JCLASS_FIELD(Long, sevenZipArchiveInstance)
/*    */JCLASS_FIELD(Long, sevenZipArchiveInStreamInstance)
/*    */JCLASS_FINAL_METHOD(Void, setArchiveFormat, "(Ljava/lang/String;)V")
END_JCLASS

#endif /* IINARCHIVE_H_ */
