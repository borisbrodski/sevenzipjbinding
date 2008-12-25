#ifndef SEVENZIPBINDINGEXCEPTION_H_
#define SEVENZIPBINDINGEXCEPTION_H_

#define EXCEPTION_MESSAGE_MAX_SIZE 4000

class SevenZipBindingException : public Object
{
private:
    char * message;
public:
    SevenZipBindingException(char * fmt, ...);
    
    virtual ~SevenZipBindingException() {
        // TRACE_OBJECT_DESTRUCTION
    }
    char * GetMessage()
    {
        TRACE_OBJECT_CALL("GetMessage")
        return message;
    }
};

#endif /*SEVENZIPBINDINGEXCEPTION_H_*/
