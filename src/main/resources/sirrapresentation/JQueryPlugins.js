jQuery.fn.setText = function(textObject) {

	if(textObject == null) textObject = "";
	
	if(textObject.isHtmlWrapper == true) {
		this.html(textObject.text);
	}
	else {
		this.text(textObject);
	}
	
	return this;
};

jQuery.fn.enter = function(enterMethod) {

	if(enterMethod == null) return;
	
	this.keypress(function(e) {
		if(e.shiftKey) return;
		
	    if(e.keyCode == 13) { // Enter
	    	enterMethod();
	    }
	});
	
	return this;
};

jQuery.fn.disableTextSelect = function() {
	this.css({
		"-ms-user-select": "none", /* IE 10+ */
		"-moz-user-select": "-moz-none",
		"user-select": "none",
		"-webkit-user-select": "none"
	});
}
