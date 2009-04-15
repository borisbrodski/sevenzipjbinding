#ifndef SEVENZIPEXCEPTION_H_
#define SEVENZIPEXCEPTION_H_

#define EXCEPTION_MESSAGE_MAX_SIZE 4000

// TODO Rename to SevenZipException
class SevenZipException : public Object
{
private:
    char * _message;
    HRESULT _hresult;
public:
    SevenZipException(const char * fmt, ...);
    SevenZipException(HRESULT hresult, char * fmt, ...);

    virtual ~SevenZipException() {
        if (_message)
        {
            free(_message);
        }
    }
    char * GetMessage()
    {
        TRACE_OBJECT_CALL("GetMessage")
        return _message;
    }

    HRESULT GetHResult()
    {
        TRACE_OBJECT_CALL("GetHResult")
        return _hresult;
    }
};

#endif /*SEVENZIPEXCEPTION_H_*/
