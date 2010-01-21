/*
 * JObjectList.h
 *
 *  Created on: Jan 18, 2010
 *      Author: boris
 */

#ifndef JOBJECTLIST_H_
#define JOBJECTLIST_H_

#include <iostream> // TODO Remove me

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
    VALUE & add(JOBJ & jobj) {
        jobjectList.push_front(Item());
        Item & item = *jobjectList.begin();
        item.jobj = jobj;
        return item.value;
    }

    VALUE * get(JNIEnv * env, JOBJ jobj) {
        iterator iter = jobjectList.begin();
        for (; iter != jobjectList.end(); iter++) {
            std::cout << "MAP: Checking " << iter->jobj << std::endl;
            if (env->IsSameObject(iter->jobj, jobj)) {
                std::cout << "MAP: Found!" << std::endl;
                if (jobjectList.begin() != iter) {
                    std::cout << "MAP: Move" << std::endl;
                    // Move found class to the front
                    jobjectList.splice(jobjectList.begin(), jobjectList, iter);
                }
                // Put element on top
                return &(iter->value);
            }
        }


        return NULL;
    }
};

#endif /* JOBJECTLIST_H_ */
