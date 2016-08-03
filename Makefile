ME=$(shell basename $(shell pwd))
LEIN=$(shell which lein || echo $(HOME)/bin/lein)

CSS=build/css/site.min.css build/css/spinner.min.css
APP=build/js/app.js
IDX=build/index.html
FNT=build/fonts

all: $(APP) $(CSS) $(IDX) $(FNT)

$(CSS): resources/public/css/*.css
	$(LEIN) minify-assets

$(APP): src/clj*/** project.clj
	rm -f $(APP)
	$(LEIN) cljsbuild once min

$(IDX): src/clj/**/handler.clj
	dev=no lein index-html > $(IDX)

$(FNT): resources/public/fonts/**/*
	cp -av resources/public/fonts build/fonts

clean:
	$(LEIN) clean
	rm -rf build

