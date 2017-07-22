import 'dart:async';
import 'dart:convert';
import 'dart:html';

import 'package:angular2/angular2.dart';
import 'package:http/http.dart';

@Component(selector: 'login', templateUrl: 'LoginComponent.html')
class LoginComponent {
	static const _loginUrl = '../login.jsp';
	static const _logoutUrl = '../logout';
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
			final response = await _http.post(url, headers: _headers, body: {"username" : username, "password" : password});
			loggedIn = true; 
		}
		catch(e)	{
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
}
