import '../../model/box.dart';
import 'package:angular2/angular2.dart';
import 'dart:async';
import 'dart:convert';
import 'package:http/http.dart';

@Injectable()
class BoxService {
  static final _headers = {'Content-Type': 'application/json'};
  static const _boxUrl = 'rest/types';
  final Client _http;

  BoxService(this._http);

  Future<Box> getBox(String name) async {
    try {
      final response = await _http.get(_boxUrl + '/'+ name);
      return new Box.fromJson(_extractData(response));
    } catch(e) {
      throw _handleError(e);
    }
  }

  dynamic _extractData(Response resp) => JSON.decode(resp.body)['data'];

  Exception _handleError(dynamic e) {
    return new Exception('Server error; cause: $e');
  }

}
