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

ClassUtilStatic.prototype.serializable = function(currentClass, typeName, attributeArray) {
	
	for(var i=0; i<attributeArray.length; i++) {
		var argName = attributeArray[i];
		
		var capitalArgName = argName.substring(0, 1).toUpperCase() + argName.substring(1);
		
		if(currentClass.prototype["get" + capitalArgName] == null) {
			currentClass.prototype["get" + capitalArgName] = function(argName) {
				return function() {
					return this[argName];
				};
			}(argName);
		}
		
		if(currentClass.prototype["set" + capitalArgName] == null) {
			currentClass.prototype["set" + capitalArgName] = function(argName) {
				return function(value) {
					this[argName] = value;
				};
			}(argName);
		}
	}
	
	currentClass.prototype._s_attributes = attributeArray;
	currentClass.prototype._s_attributes.push("_s_type");
	currentClass.prototype._s_type = typeName;
	
	currentClass.prototype._s_replacer = function(key, value) {
		value;
	}
	
	currentClass.prototype._toJSON = function() {
		currentClass.prototype.toJSON = null;
		var str = JSON.stringify(this, currentClass.prototype._s_attributes);
		currentClass.prototype.toJSON = currentClass.prototype._toJSON;
		
		var obj = JSON.parse(str);
		return obj;
	}
	
	currentClass.prototype.toJSON = currentClass.prototype._toJSON;
}

function _s_toJSON() {
	PersonCard.prototype.toJSON = null;
	var str = JSON.stringify(this, ["email"]);
	PersonCard.prototype.toJSON = PersonCard.prototype._toJSON;
	
	return str;
}

var ClassUtil = new ClassUtilStatic();