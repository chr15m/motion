ME=$(shell basename $(shell pwd))
LEIN=$(shell which lein || echo $(HOME)/bin/lein)

CSS=build/css/site.min.css build/css/spinner.min.css
APP=build/js/app.js
IDX=build/index.html

all: $(APP) $(CSS) $(IDX)

$(CSS): resources/public/css/*.css
	$(LEIN) minify-assets

$(APP): src/clj*/** project.clj
	rm -f $(APP)
	$(LEIN) cljsbuild once min

$(IDX): src/clj/**/handler.clj
	dev=no lein index-html > $(IDX)

clean:
	$(LEIN) clean
	rm -rf build

