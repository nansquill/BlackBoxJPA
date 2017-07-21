import 'dart:async';

import 'package:angular2/angular2.dart';

import 'package:blackbox/models/Category.dart';
import 'package:blackbox/models/MockCategories.dart';

@Injectable()
class CategoryService {
	Future<List<Category>> getCategories() async => MockCategories;
	
	Future<List<Category>> getCategoriesSlowly() {
		return new Future<List<Categories>>.delayed(
			const Duration(seconds: 2), getCategories
		);
	}
	
	Future<Category> getCategory(String name) async =>
		(await getCategories()).firstWhere((category) => category.name == name);
}