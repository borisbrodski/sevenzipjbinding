/*
 * PropertyInfo.h
 *
 *  Created on: Jan 18, 2010
 *      Author: boris
 */

#ifndef PROPERTYINFO_H_
#define PROPERTYINFO_H_

#include "JavaStaticInfo.h"

JT_BEGIN_CLASS("net/sf/sevenzipjbinding", PropertyInfo)
    JT_FIELD(String, name)
    JT_FIELD_OBJECT(propID, "Lnet/sf/sevenzipjbinding/PropID;")
    JT_FIELD(Class, varType)
JT_END_CLASS

#endif /* PROPERTYINFO_H_ */
