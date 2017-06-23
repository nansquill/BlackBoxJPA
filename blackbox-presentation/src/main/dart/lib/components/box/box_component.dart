import '../../model/box.dart';
import 'box_service.dart';
import 'dart:async';
import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';

@Component(
  selector: 'box',
  template: 'box_component.html',
  styleUrls: const ['box_component.css'],
  providers: const [BoxService]
)

class BoxComponent implements OnInit {
  Box box;

  final BoxService _boxService;
  final Router _router;
  final RouteParams _routeParams;

  BoxComponent(this._boxService, this._router, this._routeParams);

  Future<Null> ngOnInit() async {
    var name = _routeParams.get('name');
    
    if (name != null && name != '') {
      box = await (_boxService.getBox(name));
    }
  }

}