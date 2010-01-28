/*
 * PropertyInfo.h
 *
 *  Created on: Jan 18, 2010
 *      Author: boris
 */

#ifndef PROPERTYINFO_H_
#define PROPERTYINFO_H_

#include "JavaStaticInfo.h"

BEGIN_JCLASS("net/sf/sevenzipjbinding", PropertyInfo)
    JCLASS_FIELD(String, name)
    JCLASS_FIELD_OBJECT(propID, "Lnet/sf/sevenzipjbinding/PropID;")
    JCLASS_FIELD(Class, varType)
END_JCLASS

#endif /* PROPERTYINFO_H_ */
