import 'dart:async';
import 'dart:convert';

import 'package:angular2/angular2.dart';
import 'package:http/http.dart';

import '../../models/Category.dart';
import '../../models/MockCategories.dart';

@Injectable()
class CategoryService{

	static const _categoryUrl = '../rest/types'; //URL to web api
	static final _headers = {'Content-Type': 'application/json', 'Accept':'application/json'};
	final Client _http;
	
	CategoryService(this._http);	

	Future<List<Category>> getCategories() async {
		try	{
			final response = await _http.get(_categoryUrl);
			final categories = _extractData(response)
				.map((value) => new Category.fromJson(value))
				.toList();
			final lg = categories.length;
			print("[Info] Received $lg categories");
			return categories;
		}
		catch(e) {
			throw _handleError(e);
		}
	}
		
	Future<Category> getCategory(String name) async {
		try	{
			final response = await _http.get('$_categoryUrl/$name');
			Category cat = new Category.fromJson(_extractData(response));
			print("[Info] Receive category $name");
			return cat;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	Future<Category> update(Category category) async {
		try	{
			final url = '$_categoryUrl/update';
			final response = 
				await _http.put(url, headers:_headers, body: JSON.encode(category));
			Category cat = new Category.fromJson(_extractData(response));
			final name = cat.name;
			print("[Info] Updated category $name");
			return cat;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}

	Future<Category> create(Category category) async {
		try	{
			final response = await _http.post(_categoryUrl, headers: _headers, body: JSON.encode(category));
			Category cat = new Category.fromJson(_extractData(response));
			final name = cat.name;
			print("[Info] Created category $name");
			return cat;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	Future<Category> delete(String name) async {
		try {
			final url = '$_categoryUrl/delete/$name';
			final Response = await _http.delete(url, headers: _headers);
			print("[Info] Deleted category $name");
		}
		catch(e)	{
			throw _handleError(e);
		}
	}

	Future<List<Category>> getCategoriesMock() async => MockCategories;
			
	dynamic _extractData(Response resp) => JSON.decode(resp.body);
	
	Exception _handleError(dynamic e)	{
		print("[Error] $e");
		return new Exception('Server error; cause: $e');
	}	
}