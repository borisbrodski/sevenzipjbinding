
rem if [ "$*" == "" ] ; then
rem     echo "Usage upload-release.bat <release-file-name> [<description>]"
rem     exit 1
rem fi

for %%i in (%1) do curl -T %%i -H Filename:%%i -H Descr:%2 http://sevenzipjbind.sourceforge.net/upload.php