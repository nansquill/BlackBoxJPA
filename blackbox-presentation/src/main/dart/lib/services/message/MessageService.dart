import 'dart:async';

import 'package:angular2/angular2.dart';

import '../../models/Message.dart';
import '../../models/MockMessages.dart';

@Injectable()
class MessageService {
	Future<List<Message>> getMessages() async => MockMessages;
	
	Future<List<Message>> getMessagesSlowly() {
		return new Future<List<Message>>.delayed(
			const Duration(seconds: 2), getMessages
		);
	}
	
	Future<Message> getMessage(int id) async =>
		(await getMessages()).firstWhere((message) => message.id == id);
		
	Future<List<Message>> getMessagesByCategory(Category category) async
	{
		List<Message> messages = await getMessages();
		List<Message> selected;
		
		
		messages.forEach((message) {
			Category cat = message.category;
			
			if(cat.name == category.name)
			{
				selected.add(message);
			}
		});
		
		return selected;
	}
}