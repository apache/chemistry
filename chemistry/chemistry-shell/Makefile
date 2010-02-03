VERSION:=0.5
POM_VERSION:=$(VERSION)-SNAPSHOT
TODAY:=$(shell date +"%Y%m%d")

#RELEASE:=$(VERSION)-$(TODAY)
#RELEASE_DIR:=cmissh-$(VERSION)-$(TODAY)
RELEASE:=$(TODAY)
RELEASE_DIR:=cmissh-$(TODAY)

.PHONY: all clean build release install push

all:	build test

clean:
	mvn clean
	find . -name "*~" | xargs rm -f
	find . -name "*,orig" | xargs rm -f

build:
	mvn package
	mkdir -p target/$(RELEASE_DIR)
	cp scripts/* target/$(RELEASE_DIR)
	cp README.txt target/$(RELEASE_DIR)
	cp target/chemistry-shell-$(POM_VERSION).jar \
		target/$(RELEASE_DIR)/chemistry-shell.jar
	cd target ; zip -r $(RELEASE_DIR).zip $(RELEASE_DIR)

test:
	cd scripts ; ../run.sh -t -b testscript

install: release
	cd target/$(RELEASE_DIR) ; python install.py

push:
	rsync -e ssh target/$(RELEASE_DIR).zip \
		zope@gironde.nuxeo.com:static/nuxeo.org/cmis/

