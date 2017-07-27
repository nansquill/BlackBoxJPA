import 'dart:async';
import 'dart:convert';
import 'dart:html';

import 'package:angular2/angular2.dart';
import 'package:http/http.dart';

@Component(selector: 'login', templateUrl: 'LoginComponent.html')
class LoginComponent {
	static const _loginUrl = '../login.jsp';
	static const _logoutUrl = '../logout';
	static const _registerUrl = '../rest/register';
	static final _headers = {'Content-Type': 'application/json'};
	final Client _http;

	LoginComponent(this._http);

	String username;
	String password;
	bool loggedIn;
	bool rememberMe = true;	

	Future<Null> getLogin() async {
		
		try	{
			final url = '$_loginUrl';
			final response = await _http.post(url, body: {"username" : username, "password" : password});
			dynamic urlreg = '$_registerUrl/auth';
			await _http.get(urlreg, headers: _headers);
			loggedIn = true; 
		}
		catch(e)	{
			loggedIn = false;
			throw _handleError(e);
		}
	}
	
	Future<Null> getLogout() async {
		
		try	{
			final url = '$_logoutUrl';
			final response = await _http.get(url);
			loggedIn = false;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	bool isLoggedIn() => loggedIn;
	
	void login(dynamic e){
		e.preventDefault();
		this.rememberMe = rememberMe;
		HttpRequest.postFormData("../login.jsp", { "username" : username, "password" : password })
			.then((request) {
				loggedIn = true;
			}).catchError((n)=>print(n));
	}

	void logout(dynamic e){
		e.preventDefault();
		HttpRequest.request("../logout", method: "GET")
			.then((request) {loggedIn = false; print(request.getAllResponseHeaders());})
			.catchError((n)=>print(n));
    }
    
    Exception _handleError(dynamic e)	{
		print(e);
		return new Exception('Server error; cause: $e');
	}
}
