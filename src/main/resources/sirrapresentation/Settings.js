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