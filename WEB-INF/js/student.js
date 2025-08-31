class aaa
{
postTypeResponse()
{
}
getTypeResponse()
{
}
}
class bbb
{
postTypeResponse()
{
}
getTypeResponse()
{
}
}
class ccc
{
postTypeResponse()
{
}
getTypeResponse()
{
}
}
class DAOConnection
{
getConnection()
{
}
}
class login
{
login(arg0,arg1,arg2)
{
}
}
class Services
{
runOnStartup1()
{
}
postTypeResponse()
{
}
runOnStartup2()
{
}
runOStartup3(arg0)
{
}
getTypeResponse()
{
}
}
class Services2
{
classNo;
sessionScope;
servletContext;
runOnStartup1()
{
}
setSessionScope(arg0)
{
this.sessionScope=arg0;
}
postTypeResponse(arg0)
{
}
postTypeResponse(arg0,arg1,arg2)
{
}
getSessionScope()
{
return this.sessionScope;
}
getTypeResponse(arg0,arg1)
{
}
}
class Student
{
rollNumber;
name;
gender;
getName()
{
return this.name;
}
setName(arg0)
{
this.name=arg0;
}
getRollNumber()
{
return this.rollNumber;
}
setRollNumber(arg0)
{
this.rollNumber=arg0;
}
getGender()
{
return this.gender;
}
setGender(arg0)
{
this.gender=arg0;
}
}
class StudentService
{
update(arg0)
{
var requesturl='//my//StudentService//update';
var promise = new Promise(function(resolve,reject){
$.ajax({url:requesturl,
type:'POST',
contentType:'application/json',
data:JSON.stringify(arg0),
success:function(response)
{
resolve(response);
},
error: function(jqXHR, textStatus, errorThrown)
{
let message = errorThrown || textStatus || 'Unknown AJAX error';
reject(new Error(message));
}
});
});
return promise;
}
add(arg0)
{
var requesturl='//my//StudentService//add';
var promise = new Promise(function(resolve,reject){
$.ajax({url:requesturl,
type:'POST',
data:JSON.stringify(arg0),
contentType:'application/json',
success:function(response)
{
resolve(response);
},
error:function(jqXHR, textStatus, errorThrown)
{
let message = errorThrown || textStatus || 'Unknown AJAX error';
reject(new Error(message));
}
});
});
return promise;
}
delete(arg0)
{
var requesturl='//my//StudentService//delete?rollNumber='+arg0;
var promise =new Promise(function(resolve,reject){
$.ajax({url:requesturl,
type:'DELETE',
success:function(response)
{
resolve(response);
},
error: function(jqXHR, textStatus, errorThrown)
{
let message = errorThrown || textStatus || 'Unknown AJAX error';
reject(new Error(message));
}
});
});
return promise;
}
getAll()
{
var requesturl='//my//StudentService//getAll';
var promise = new Promise(function(resolve,reject){
$.ajax({url:requesturl,
type:'GET',
success:function(response)
{
resolve(response);
},
error: function(jqXHR, textStatus, errorThrown)
{
let message = errorThrown || textStatus || 'Unknown AJAX error';
reject(new Error(message));
}
});
});
return promise;
}
getByRollNumber(arg0)
{
var requesturl='//my//StudentService//getByRollNumber?rollNumber='+arg0;
var promise = new Promise(function(resolve,reject){
$.ajax({url:requesturl,
type:'GET',
success:function(response)
{
resolve(response);
},
error: function(jqXHR, textStatus, errorThrown)
{
let message = errorThrown || textStatus || 'Unknown AJAX error';
reject(new Error(message));
}
});
});
return promise;
}
}
