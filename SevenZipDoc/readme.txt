--- Welcome to 7-Zip-JBinding readme file ---

7-Zip-JBinding is a free binding from java to 7-Zip free compress/decompress library (http://www.7-zip.org/)



License Info
------------

7-Zip-JBinding and 7-Zip binarys are licensed under GNU LGPL.
For more information about 7-Zip source code licenses please see
7-Zip read me file.



GNU LGPL information:
---------------------

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA



License notes
-------------

You can support development of 7-Zip by registering.

7-Zip/7-Zip-JBinding is free software distributed under the GNU LGPL.
If you need license with other conditions, write to
http://www.7-zip.org/support.html



How to test this library
------------------------

0. Warning
Be aware: This is ALPHA-release, so it contains many errors.
Please report bug fixes and code patched at
http://sourceforge.net/projects/sevenzipjbind/


1. Get program compiled and started

To use 7-Zip-JBinding you will need
  - 7z.dll 				in your current directory or %SystemRoot%\System32
  						(This dll comes from 7-Zip project:
  						http://sourceforge.net/projects/sevenzip/)
  - 7-Zip-JBinding.jar 	in your classpath 
  - 7-Zip-JBinding.dll 	in your library path
    This can be done by using "-Djava.library.path=<path to dll>"
    start parameter for java.exe or javaw.exe.
    
For example, if you put the content of 7-Zip-JBindind-X.X.zip into
C:\JavaLib\7-Zip-JBinding folder, you can compile and run your test program
with following two commands:

Compile:
  C:\Java\7-Zip-JTest>javac -cp C:\JavaLib\7-Zip-JBinding\7-Zip-JBinding.jar *
  
Run:
  C:\Java\7-Zip-JTest>java -cp C:\JavaLib\7-Zip-JBinding\7-Zip-JBinding.jar 
                           -Djava.library.path=C:\JavaLib\7-Zip-JBinding\7-Zip-JBinding.dll
                           -cp . TestMain


2. Minimal test program

You can test 7-Zip-JBinding by try to extract files from archive.
First you need to open archive. To do this you have to implement
'IInStream' interface or use a standard implementation provided within
this library:
	IInStream is = new InStreamImpl(new RandomAccessFile(new File(filename), "r"))

Then you can call SevenZip.openInArchive(...) method, providing archive format
(for example ArchiveFormat.RAR). If anything ok, you will get an archive object
which implements IInArchive interface.

Now, for example, to get count of items in archive you can simply call
getNumberOfItems() of IInArchive interface. To extract a file you will have
to implement ISequentialOutStream and IArchiveExtractCallback to provide
extraction parameter and get extracted data.

For more details please see 'net.sf.sevenzip.test.*' in src directory.