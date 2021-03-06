function Settings(settings) {
	this.isSettings = true;
	this.settings = settings;
	if(this.settings == null) this.settings = {};
}

Settings.prototype.has = function(key) {
	return this.settings.hasOwnProperty(key);
};

Settings.prototype.get = function(key, defaultValue) {
	if(this.settings.hasOwnProperty(key)) {
		return this.settings[key];
	} else {
		return defaultValue;
	}
};

/**
 * Apply classes and css standard settings onto jquery object.
 */
Settings.prototype.apply = function(jqObject) {

	var classes = this.get("classes", []);
	for(var i=0; i<classes.length; i++) {
		jqObject.addClass(classes[i]);
	}
	
	if(this.has("css")) {
		jqObject.css(this.get("css"));
	}
};