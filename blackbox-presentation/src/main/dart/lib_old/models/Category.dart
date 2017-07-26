class Category	{
	String name;
	
	Map toJson() => {'name' : name};
	
	factory Category.fromJson(Map<String, dynamic> cat) => new Category(cat['name']);
	
	Category(this.name);
}