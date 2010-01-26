/*
 * IInArchive.h
 *
 *  Created on: Jan 16, 2010
 *      Author: boris
 */

#ifndef IINARCHIVE_H_
#define IINARCHIVE_H_

#include "JavaStaticInfo.h"

JAVA_FINAL_CLASS("net/sf/sevenzipjbinding/impl", InArchiveImpl) {
    JAVA_FIELD(Long, bindingSession, "J")
    JAVA_FIELD(Long, sevenZipArchiveInstance, "J")
    JAVA_FIELD(Long, sevenZipArchiveInStreamInstance, "J")

    JAVA_FINAL_CLASS_METHOD(Void, setArchiveFormat, "(Ljava/lang/String;)V")
}


#endif /* IINARCHIVE_H_ */
