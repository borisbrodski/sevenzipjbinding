/*
 * JObjectList.h
 *
 *  Created on: Jan 18, 2010
 *      Author: boris
 */

#ifndef JOBJECTLIST_H_
#define JOBJECTLIST_H_

#include <list>
#include "jni.h"
#include "BaseSystem.h"
#include "Debug.h"


/**
 * Object map
 */
template<typename VALUE>
class JObjectMap {
    struct Item {
        jobject jobj;
        VALUE value;
    };
    std::list<Item> jobjectList;
    typedef typename std::list<Item>::iterator iterator;
public:
    void add(jobject const jobj, VALUE value) {
        jobjectList.push_front(Item());
        Item & item = *jobjectList.begin();
        item.jobj = jobj;
        item.value = value;
    }

    VALUE * get(JNIEnv * env, jobject const jobj) {
        iterator iter = jobjectList.begin();
        for (; iter != jobjectList.end(); iter++) {
            TRACE("MAP: Checking " << env << iter->jobj);
            if (env->IsSameObject(iter->jobj, jobj)) {
                TRACE("MAP: Found!")
                if (jobjectList.begin() != iter) {
                    TRACE("MAP: Move");
                    // Move found class to the front
                    jobjectList.splice(jobjectList.begin(), jobjectList, iter);
                }
                // Put element on top
                VALUE * result = &(iter->value);
                return result;
            }
        }
        TRACE("MAP: Not found");
        return NULL;
    }

    int size() {
        return jobjectList.size();
    }
};

#endif /* JOBJECTLIST_H_ */
