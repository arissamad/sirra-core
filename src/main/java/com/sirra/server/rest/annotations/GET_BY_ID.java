package com.sirra.server.rest.annotations;

import java.lang.annotation.*;

/**
 * Instead of annotating with @GET, you can annotate with @GET_BY_ID for the specific case of retrieving a single record by ID.
 * 
 * More specifically:
 * 
 *  - The caller is passing in an ID as the first path parameter, i.e. /events/2343
 *  - That path parameter corresponds to the first parameter of the method.
 *  
 * @author aris
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GET_BY_ID {

}
