MKDIR = mkdir -p

DEST_HOME=/usr/local
DEST_BIN=$(DEST_HOME)/bin
DEST_SHARE=$(DEST_HOME)/lib/p7zip
DEST_SHARE_DOC=$(DEST_HOME)/share/doc/p7zip
DEST_MAN=$(DEST_HOME)/man

.PHONY: default all all2 7za 7zG 7zFM sfx 7z 7zr Client7z common common7z LangAndHelp clean tar_bin depend test test_7z test_7zr test_7zG test_Client7z LzmaCon test_LzmaCon  all_test app cppcheck

include makefile.machine

default:7za

all:7za sfx

all2: 7za sfx 7z

all3: 7za sfx 7z 7zr

all4: 7za sfx 7z 7zr Client7z LzmaCon

all5: 7za sfx 7z 7zr 7zG 7zFM

all6: 7za sfx 7z 7zr Client7z LzmaCon 7zG 7zFM

all_test : test test_7z test_7zr test_LzmaCon test_Client7z

other: common
	$(MAKE) -C CPP/myWindows              all
	$(MAKE) -C Utils/CPUTest/MemLat       all
	$(MAKE) -C Utils/CPUTest/PipeLen      all
	$(MAKE) -C CPP/7zip/UI/P7ZIP          all
	$(MAKE) -C CPP/7zip/TEST/TestUI       all

other2:
	$(MAKE) -C CPP/7zip/Bundles/AloneGCOV all

common:
	$(MKDIR) bin

7za: common
	$(MAKE) -C CPP/7zip/Bundles/Alone all

7zr: common
	$(MAKE) -C CPP/7zip/Bundles/Alone7z all

Client7z: common
	$(MKDIR) bin/Codecs
	$(MAKE) -C CPP/7zip/Bundles/Format7zFree all
	$(MAKE) -C CPP/7zip/UI/Client7z          all

app: common 7zFM 7zG 7z sfx
	rm -fr               p7zip.app
	$(MKDIR)             p7zip.app
	cp -rp GUI/Contents  p7zip.app/
	$(MKDIR)             p7zip.app/Contents/MacOS
	cp bin/7zFM          p7zip.app/Contents/MacOS/
	cp bin/7zG           p7zip.app/Contents/MacOS/
	cp bin/7z.so         p7zip.app/Contents/MacOS/
	cp bin/7zCon.sfx     p7zip.app/Contents/MacOS/
	cp -rp bin/Codecs    p7zip.app/Contents/MacOS/
	cp -rp GUI/Lang      p7zip.app/Contents/MacOS/
	cp -rp GUI/help      p7zip.app/Contents/MacOS/

depend:
	$(MAKE) -C CPP/7zip/Bundles/Alone         depend
	$(MAKE) -C CPP/7zip/Bundles/Alone7z       depend
	$(MAKE) -C CPP/7zip/Bundles/SFXCon        depend
	$(MAKE) -C CPP/7zip/UI/Client7z           depend
	$(MAKE) -C CPP/7zip/UI/Console            depend
	$(MAKE) -C CPP/7zip/Bundles/Format7zFree  depend
	$(MAKE) -C CPP/7zip/Compress/Rar          depend
	$(MAKE) -C CPP/7zip/UI/GUI                depend
	$(MAKE) -C CPP/7zip/UI/FileManager        depend

sfx: common
	$(MKDIR) bin
	$(MAKE) -C CPP/7zip/Bundles/SFXCon  all

common7z:common
	$(MKDIR) bin/Codecs
	$(MAKE) -C CPP/7zip/Bundles/Format7zFree all
	$(MAKE) -C CPP/7zip/Compress/Rar         all

7z: common7z
	$(MAKE) -C CPP/7zip/UI/Console           all

LangAndHelp:
	cd bin ; rm -f Lang ; ln -s ../GUI/Lang .
	cd bin ; rm -f help ; ln -s ../GUI/help .

7zG: common7z LangAndHelp
	$(MAKE) -C CPP/7zip/UI/GUI               all

7zFM: common7z LangAndHelp
	$(MAKE) -C CPP/7zip/UI/FileManager       all

clean:
	$(MAKE) -C CPP/myWindows                 clean
	$(MAKE) -C CPP/7zip/Bundles/Alone        clean
	$(MAKE) -C CPP/7zip/Bundles/Alone7z      clean
	$(MAKE) -C CPP/7zip/Bundles/SFXCon       clean
	$(MAKE) -C CPP/7zip/UI/Client7z          clean
	$(MAKE) -C CPP/7zip/UI/Console           clean
	$(MAKE) -C CPP/7zip/UI/FileManager       clean
	$(MAKE) -C CPP/7zip/UI/GUI               clean
	$(MAKE) -C CPP/7zip/Bundles/Format7zFree clean
	$(MAKE) -C CPP/7zip/Compress/Rar         clean
	$(MAKE) -C CPP/7zip/Bundles/LzmaCon      clean2
	$(MAKE) -C CPP/7zip/Bundles/AloneGCOV    clean
	$(MAKE) -C CPP/7zip/TEST/TestUI          clean
	$(MAKE) -C CPP/ANDROID                   clean
	$(MAKE) -C Utils/CPUTest/MemLat          clean
	$(MAKE) -C Utils/CPUTest/PipeLen         clean
	$(MAKE) -C CPP/7zip/UI/P7ZIP             clean
	rm -fr bin
	rm -fr p7zip.app
	rm -f  Lang
	rm -fr CPP/7zip/P7ZIP.*
	rm -fr CPP/7zip/CMAKE/P7ZIP.*
	rm -fr CPP/7zip/PREMAKE/P7ZIP.*
	rm -f  CPP/7zip/QMAKE/*/*.o
	rm -f  CPP/7zip/QMAKE/*/Makefile
	rm -f  CPP/7zip/QMAKE/*/*.pro.user
	rm -f  CPP/7zip/QMAKE/*/*.x
	-find . -name "build*"    -exec rm -fr {} \;
	-find . -name "*-build-*" -exec rm -fr {} \;
	-find . -name "*.user"    -exec rm -f {} \;
	-find . -name "._*"       -exec rm -f {} \;
	rm -fr CPP/7zip/ANDROID/libs
	rm -fr CPP/7zip/ANDROID/obj
	rm -f make.log 1 2 cppcheck.out
	rm -f check/7z.so
	rm -fr p7zip.app/Contents/MacOS
	find . -name "*~"        -exec rm -f {} \;
	find . -name "*.orig"    -exec rm -fr {} \;
	find . -name ".*.swp"    -exec rm -f {} \;
	find . -name "*.[ch]"    -exec chmod -x {} \;
	find . -name "*.cpp"     -exec chmod -x {} \;
	find . -name "*.asm"     -exec chmod -x {} \;
	find . -name "makefile*" -exec chmod -x {} \;
	find . -name ".DS_Store" -exec rm -f {} \;
	find . -name "._*"       -exec rm -f {} \;
	find . -name "*.pyc"     -exec rm -f {} \;
	chmod -x ChangeLog README TODO man1/* DOC/*.txt
	chmod +x contrib/VirtualFileSystemForMidnightCommander/u7z
	chmod +x contrib/gzip-like_CLI_wrapper_for_7z/p7zip
	chmod +x install.sh check/check.sh check/clean_all.sh check/check_7zr.sh 
	cd check                  ; ./clean_all.sh

test: 7za sfx
	cd check ; TOOLS="${TOOLS}" ./check.sh "`pwd`/../bin/7za"

test_7z: 7z sfx
	cd check ; TOOLS="${TOOLS}" ./check.sh "`pwd`/../bin/7z"

test_7zr: 7zr sfx
	cd check ; TOOLS="${TOOLS}" ./check_7zr.sh "`pwd`/../bin/7zr"

test_7zG: 7zG sfx
	cd check ; TOOLS="${TOOLS}" ./check.sh "`pwd`/../bin/7zG"

LzmaCon:
	$(MAKE) -C CPP/7zip/Bundles/LzmaCon

test_LzmaCon:
	$(MAKE) -C CPP/7zip/Bundles/LzmaCon  test

test_Client7z: Client7z
	cd check ; TOOLS="${TOOLS}" ./check_Client7z.sh "`pwd`/../bin/Client7z"

install:
	./install.sh $(DEST_BIN) $(DEST_SHARE) $(DEST_MAN) $(DEST_SHARE_DOC) $(DEST_DIR)

cppcheck:
	cppcheck -f -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE -DNDEBUG -D_REENTRANT -DENV_UNIX -D_7ZIP_LARGE_PAGES -DBREAK_HANDLER -DUNICODE -D_UNICODE   . 2>&1 | tee -i cppcheck.out

REP=$(shell pwd)
ARCHIVE=$(shell basename $(REP))

.PHONY: tar_all tar_all2 src_7z tar_bin tar_bin2

tar_all : clean
	rm -f  ../$(ARCHIVE)_src_all.tar.bz2
	cp makefile.linux_any_cpu makefile.machine
	cd .. ; (tar cf - $(ARCHIVE) | bzip2 -9 > $(ARCHIVE)_src_all.tar.bz2)

tar_all2 : clean
	rm -f  ../$(ARCHIVE)_src_all.tar.bz2
	cp makefile.linux_any_cpu makefile.machine
	cd .. ; (tar cf - $(ARCHIVE) | 7za a -mx=9 -tbzip2 -si $(ARCHIVE)_src_all.tar.bz2 )

src_7z : clean
	rm -f  ../$(ARCHIVE)_src.7z
	cd .. ; 7za a -mx=9 -m0=ppmd:mem=128m:o=32 $(ARCHIVE)_src.7z $(ARCHIVE)

tar_bin:
	rm -f  ../$(ARCHIVE)_x86_linux_bin.tar.bz2
	chmod +x install.sh contrib/VirtualFileSystemForMidnightCommander/u7z contrib/gzip-like_CLI_wrapper_for_7z/p7zip
	cd .. ; (tar cf - $(ARCHIVE)/bin $(ARCHIVE)/contrib $(ARCHIVE)/man1 $(ARCHIVE)/install.sh $(ARCHIVE)/ChangeLog $(ARCHIVE)/DOC $(ARCHIVE)/README $(ARCHIVE)/TODO | bzip2 -9 > $(ARCHIVE)_x86_linux_bin.tar.bz2)

tar_bin2:
	rm -f  ../$(ARCHIVE)_x86_linux_bin.tar.bz2
	chmod +x install.sh contrib/VirtualFileSystemForMidnightCommander/u7z contrib/gzip-like_CLI_wrapper_for_7z/p7zip
	cd .. ; (tar cf - $(ARCHIVE)/bin $(ARCHIVE)/contrib $(ARCHIVE)/man1 $(ARCHIVE)/install.sh $(ARCHIVE)/ChangeLog $(ARCHIVE)/DOC $(ARCHIVE)/README $(ARCHIVE)/TODO | 7za a -mx=9 -tbzip2 -si $(ARCHIVE)_x86_linux_bin.tar.bz2)

