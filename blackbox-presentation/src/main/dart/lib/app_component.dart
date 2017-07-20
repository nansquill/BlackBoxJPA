// Copyright (c) 2017. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.

import 'components/box/box_component.dart';
import 'dart:async';
import 'package:angular2/core.dart';
import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';

import 'package:blackbox/components/newest/newest_component.dart';
import 'package:blackbox/components/create/create_component.dart';
import 'package:blackbox/components/login/login_component.dart';
import 'package:blackbox/components/register/register_user_component.dart';

import 'components/box/box_service.dart';
import 'package:blackbox/models/box.dart';

@Component(
    selector: 'my-app',
    directives: const [ROUTER_DIRECTIVES, ShowNewest,CreateMessages,RegisterUser,LoginComponents],
    templateUrl: 'app_component.html',
    providers: const [ROUTER_PROVIDERS])

@RouteConfig(
  const [
    const Route(
      path: '/box/:name', name: 'Box', component: BoxComponent,
    ),
    // const Route(path: '/boxes', name: 'Boxes', component: BoxesList)
  ]
)

class AppComponent implements OnInit {
  final Router router;
  final BoxService boxService;

  List<Box> boxes;

  AppComponent(this.router, this.boxService);

  Future<Null> getBoxes() async {
    this.boxes = await boxService.getBoxes();
  }

  ngOnInit() {
    getBoxes();
  }
}