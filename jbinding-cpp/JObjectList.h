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


/**
 * Synchronized object map
 */
template<typename JOBJ, typename VALUE>
class JObjectMap {
    struct Item {
        JOBJ jobj;
        VALUE value;
    };
    PlatformCriticalSection _criticalSection;
    std::list<Item> jobjectList;
    typedef typename std::list<Item>::iterator iterator;
public:
    VALUE & add(JOBJ const & jobj) {
        _criticalSection.Enter();
        jobjectList.push_front(Item());
        Item & item = *jobjectList.begin();
        item.jobj = jobj;
        _criticalSection.Leave();
        return item.value;
    }

    VALUE * get(JNIEnv * env, JOBJ const & jobj) {
        _criticalSection.Enter();
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
                VALUE * result = &(iter->value);
                _criticalSection.Leave();
                return result;
            }
        }
        TRACE("MAP: Not found");

        _criticalSection.Leave();
        return NULL;
    }

    int size() {
        return jobjectList.size();
    }
};

#endif /* JOBJECTLIST_H_ */
