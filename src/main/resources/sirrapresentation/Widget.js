/**
 * For each Widget class, put the following lines on top:
 * 
 *  ClassUtil.mixin(CurrentClass, currentInstance, Widget);
 *  Widget.call(this, "MyWidgetName");
 *  
 */
function Widget(widgetName, attachNow, settings, applySettings) {
	
	if(!_sirra.widgetLibrary.hasOwnProperty(widgetName)) {
		throw new Error("Can't find widget: " + widgetName);
	}
	
	this.widget = _sirra.widgetLibrary[widgetName].clone();
	if(this.widget == null) {
		throw new Error("Widget is null: " + widgetName);
	}
	
	this.parent = current;
	
	// If null or true, it will attach now.
	if(attachNow != false) current.append(this.widget);
	
	this.settings = new Settings(settings);
	
	if(applySettings != false) {
		var classes = this.settings.get("classes", []);
		for(var i=0; i<classes.length; i++) {
			this.widget.addClass(classes[i]);
		}
		
		if(this.settings.has("css")) {
			this.widget.css(this.settings.get("css"));
		}
	}
		
	this.input = this.widget.find("input");
	if(this.input.length == 0) {
		this.input = this.widget.find("textarea");
	}
	
	this.textSection = this.widget.find(".text-section");
}

Widget.prototype.setValue = function(value) {
	if(this.input.length > 0) this.input.val(value);
	else if(this.textSection.length > 0) this.textSection.setText(value);
	else this.widget.setText(value);
};

Widget.prototype.getValue = function() {
	if(this.input.length > 0) return this.input.val();
	else if(this.textSection.length > 0) return this.textSection.text();
	return this.widget.text();
};

Widget.prototype.remove = function() {
	this.widget.remove();
};

Widget.prototype.finish = function() {
	current = this.parent;
};

/**
 * Initial widget loading code.
 */
_sirra.widgetLibrary = {};

_sirra.processWidgets = function() {
	
	var widgets = _sirra.widgetHolder.children();
	for(var i=0; i<widgets.length; i++) {
		
		var widget = $(widgets[i]);
		widget.detach();
		
		var widgetName = widget.attr("id");
		
		log("Found widget: " + widgetName);
		
		_sirra.widgetLibrary[widgetName] = widget;
	}
}
