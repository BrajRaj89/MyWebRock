class aaa
{
getTypeResponse()
{
}
postTypeResponse()
{
}
}
class bbb
{
getTypeResponse()
{
}
postTypeResponse()
{
}
}
class ccc
{
getTypeResponse()
{
}
postTypeResponse()
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
getTypeResponse()
{
}
postTypeResponse()
{
}
runOnStartup1()
{
}
runOnStartup2()
{
}
runOStartup3(arg0)
{
}
}
class Services2
{
student;
sessionScope;
servletContext;
setSessionScope(arg0)
{
this.sessionScope=arg0;
}
getTypeResponse(arg0,arg1)
{
}
postTypeResponse(arg0,arg1,arg2)
{
}
postTypeResponse()
{
}
runOnStartup1()
{
}
getSessionScope()
{
return this.sessionScope;
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
getGender()
{
return this.gender;
}
setGender(arg0)
{
this.gender=arg0;
}
setRollNumber(arg0)
{
this.rollNumber=arg0;
}
getRollNumber()
{
return this.rollNumber;
}
}
class StudentService
{
update(arg0)
{
var requesturl='/MyWebRock/framework/StudentService/update';
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
var requesturl='/MyWebRock/framework/StudentService/add';
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
var requesturl='/MyWebRock/framework/StudentService/delete?rollNumber='+arg0;
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
var requesturl='/MyWebRock/framework/StudentService/getAll';
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
var requesturl='/MyWebRock/framework/StudentService/getByRollNumber?rollNumber='+arg0;
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
class User
{
username;
balance;
}
