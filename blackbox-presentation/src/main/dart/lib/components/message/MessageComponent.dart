import 'dart:async';

import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';

import 'package:blackbox/models/Category.dart';
import 'package:blackbox/services/category/CategoryService.dart';
import 'package:blackbox/models/Message.dart';
import 'package:blackbox/services/message/MessageService.dart';

@Component(
	selector: 'messages', 
	templateUrl:'MessageComponent.html',
	styleUrls: const['MessageComponent.css'],
	directives: const[CORE_DIRECTIVES],
	pipes: const[COMMON_PIPES]	
)
class MessageComponent implements OnInit {
	final Router _router;
	final MessageService _messageService;
	final CategoryService _catService;
	
	List<Category> category;
	Category selectedCategory;
	
	List<Message> messages;
	Message selectedMessage;
	
	bool subscribeMe;
	
	MessageComponent(this._messageService, this._catService, this._router);
	
	Future<Null> getMessages() async {
		if(subscribeMe)
		{
			messages = await _messageService.getMessagesFromMe();
		}
		else if(selectedCategory != null)
		{
			messages = await _messageService.getMessagesByCategory(selectedCategory);
		}
		else
		{
			messages = await _messageService.getMessages();
		}
	}
	
	
	
	Future<Null> getCategories() async {
		categories = await _catService.getCategories();
	}
	
	void ngOnInit() { getCategories(); getMessages(); subscribeMe = false;  }
	
	void onCategoryChange(Category category) { selectedCategory = category; subscribeMe = false; selectedMessage = null; getMessages(); }
	
	void onSelect(Message message) { selectedMessage = message;}
	
	Future<Null> gotoDetail() => _router.navigate([
		'MessageDetail',
		{'id': selectedMessage.id.toString()}
	]);
}