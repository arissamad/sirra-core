/**
 * Provides mixin support.
 */
function ClassUtilStatic() {
}

/** 
 * You can mixin as many classes as you want
 */
ClassUtilStatic.prototype.mixin = function(CurrentClass, currentInstance, SuperClass, callSuperConstructor) {
	var currentPrototype = CurrentClass.prototype;
	
	if(currentPrototype.superclasses == null) {
		currentPrototype.superclasses = [];
	}
	
	for(var i=0; i<currentPrototype.superclasses.length; i++) {
		var sc = currentPrototype.superclasses[i];
		if(sc == SuperClass) {
			// Already mixed-in previously
			if(callSuperConstructor == true) {
				SuperClass.call(currentInstance);
			}
			return;
		}
	}
	
	currentPrototype.superclasses[currentPrototype.superclasses.length] = SuperClass;
	
	// Merge into current instance (which already has used the previous prototype)
	for(var attribute in SuperClass.prototype) {
		if(currentPrototype[attribute] == null) {
			currentPrototype[attribute] = SuperClass.prototype[attribute];
		}
	}
	
	if(callSuperConstructor == true) {
		SuperClass.call(currentInstance);
	}
};

var ClassUtil = new ClassUtilStatic();