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

template<typename JOBJ, typename VALUE>
class JObjectMap {
    struct Item {
        JOBJ jobj;
        VALUE value;
    };
    std::list<Item> jobjectList;
    typedef typename std::list<Item>::iterator iterator;
public:
    VALUE & add(JOBJ const & jobj) {
        jobjectList.push_front(Item());
        Item & item = *jobjectList.begin();
        item.jobj = jobj;
        return item.value;
    }

    VALUE * get(JNIEnv * env, JOBJ jobj) {
        iterator iter = jobjectList.begin();
        for (; iter != jobjectList.end(); iter++) {
            TRACE("MAP: Checking " << iter->jobj);
            if (env->IsSameObject(iter->jobj, jobj)) {
                TRACE("MAP: Found!")
                if (jobjectList.begin() != iter) {
                    TRACE("MAP: Move");
                    // Move found class to the front
                    jobjectList.splice(jobjectList.begin(), jobjectList, iter);
                }
                // Put element on top
                return &(iter->value);
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
