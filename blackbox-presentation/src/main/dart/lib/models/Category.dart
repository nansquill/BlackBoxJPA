class Category	{
	String name;
	
	factory Category.fromJson(Map<String, dynamic> cat) => new Category(cat['name']);
	
	Category(this.name);
}