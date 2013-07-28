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
    if($.browser.mozilla){
    	this.css('MozUserSelect','none');
    } else if($.browser.msie) {
    	this.bind('selectstart', function(){return false;});
    } else {
    	this.mousedown(function(){return false;});
    }
}
