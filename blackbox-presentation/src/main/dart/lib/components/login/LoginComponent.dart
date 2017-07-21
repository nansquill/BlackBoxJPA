import 'package:angular2/angular2.dart';
import 'dart:html';

@Component(selector: 'login', templateUrl: 'login_component.html')
class LoginComponent {

  String username;
  String password;
  bool loggedIn;
  bool rememberMe;

  bool isLoggedIn() => loggedIn;

  void login(dynamic e){
    e.preventDefault();
    this.rememberMe = rememberMe;
    HttpRequest.postFormData("../login.jsp", { "username" : username, "password" : password },{requestHeaders )
      .then((request) {
      	loggedIn = true;
      	  HttpRequest.postFormData("/blackbox/rest/register/login", { "username" : username, "password" : password, "rememberMe": "true" })
	      .then()
	      .catchError((n)=>print(n));
      	
      })
      .catchError((n)=>print(n));

  }

  void logout(dynamic e){
    e.preventDefault();
    HttpRequest.request("../logout", method: "GET")
      .then((request) {loggedIn = false; print(request.getAllResponseHeaders());})
      .catchError((n)=>print(n));
    HttpRequest.request("/blackbox/rest/register/logout", method: "GET")
      .then()
      .catchError((n)=>print(n));
  }
}
