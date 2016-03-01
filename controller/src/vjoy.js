/* global Phaser */

(function (window, Phaser) {
  'use strict';

  /**
   * Virtual Joystick plugin for Phaser.io
   */

   var setJoystick = true;
   var prevDeltaX = 0;
   var prevDeltaY = 0;

  Phaser.Plugin.VJoy = function (game, parent) {
    Phaser.Plugin.call(this, game, parent);

    this.isInTheZone = isInsideTheZone.bind(this);

    this.input = this.game.input;
    this.imageGroup = [];

    //this.imageGroup.push(this.game.add.sprite(0, 0, 'vjoy_cap'));
    //this.imageGroup.push(this.game.add.sprite(0, 0, 'vjoy_body'));
    
    var base = this.game.add.sprite(0, 0, 'vjoy_body');
    base.scale.setTo(0.7, 0.8);
    var top = this.game.add.sprite(0, 0, 'vjoy_base');
    top.scale.setTo(0.8,0.9);


    this.imageGroup.push(base);
    this.imageGroup.push(top);

    this.imageGroup.forEach(function (e) {
      e.anchor.set(0.5);
      e.visible = false;
      e.fixedToCamera = true;
    });
  };

  Phaser.Plugin.VJoy.prototype = Object.create(Phaser.Plugin.prototype);
  Phaser.Plugin.VJoy.prototype.constructor = Phaser.Plugin.VJoy;

  Phaser.Plugin.VJoy.prototype.settings = {
    maxDistanceInPixels: 50,
    singleDirection: false
  };


  Phaser.Plugin.VJoy.prototype.cursors = {
    up: false,
    down: false,
    left: false,
    right: false
  };

  Phaser.Plugin.VJoy.prototype.speed = {
    x: 0,
    y: 0
  };

  Phaser.Plugin.VJoy.prototype.inputEnable = function (x1, y1, x2, y2) {
    x1 = x1 || 0;
    y1 = y1 || 0;
    x2 = x2 || this.game.Width;
    y2 = y2 || this.game.height;
    var midScreenX = this.game.world.centerX;
    var midScreenY = this.game.world.centerY;

    this.zone = new Phaser.Rectangle(midScreenX*1.5,0,midScreenX*0.5 , midScreenY*2);
    this.input.onDown.add(createCompass, this);
  };

  Phaser.Plugin.VJoy.prototype.inputDisable = function () {
    this.input.onDown.remove(createCompass, this);
    this.input.onUp.remove(removeCompass, this);
  };

  var initialPoint;

  var isInsideTheZone = function isInsideTheZone(pointer) {
    return this.zone.contains(pointer.position.x, pointer.position.y);
  };

  var createCompass = function createCompass(pointer) {

    //
      if (this.pointer || this.isInTheZone(pointer)) {
        return;
      }

      this.pointer = pointer;

      this.imageGroup.forEach(function (e) {
        e.visible = true;
        e.bringToTop();

        
          e.cameraOffset.x = pointer.x;
          e.cameraOffset.y = pointer.y;
        }, this);

      this.preUpdate = setDirection.bind(this);

      if(setJoystick){
      initialPoint = this.input.activePointer.position.clone();
    }
      setJoystick = false;


  };

  var removeCompass = function () {

    this.imageGroup.forEach(function (e) {
      e.bringToTop();
      e.cameraOffset.x = initialPoint.x;
      e.cameraOffset.y = initialPoint.y;
    });

    this.cursors.up = false;
    this.cursors.down = false;
    this.cursors.left = false;
    this.cursors.right = false;

    this.speed.x = 0;
    this.speed.y = 0;

    this.preUpdate = empty;
    this.pointer = null;
  };

  var empty = function () {
  };

  var setDirection = function () {
    if (this.isInTheZone(this.pointer)) {
      return;
    }

    if (!this.pointer.active) {
      removeCompass.bind(this)();
      return;
    }

    var d = initialPoint.distance(this.pointer.position);
    var maxDistanceInPixels = this.settings.maxDistanceInPixels;

    var deltaX = this.pointer.position.x - initialPoint.x;
    var deltaY = this.pointer.position.y - initialPoint.y;

    if (this.settings.singleDirection) {
      if (Math.abs(deltaX) > Math.abs(deltaY)) {
        deltaY = 0;
        this.pointer.position.y = initialPoint.y;
      } else {
        deltaX = 0;
        this.pointer.position.x = initialPoint.x;
      }
    }

    var angle = initialPoint.angle(this.pointer.position);

    if (d > maxDistanceInPixels) {
      deltaX = Math.cos(angle) * maxDistanceInPixels;
      deltaY = Math.sin(angle) * maxDistanceInPixels;
    }

    this.speed.x = parseInt((deltaX / maxDistanceInPixels) * 100 * -1, 10);
    this.speed.y = parseInt((deltaY / maxDistanceInPixels) * 100 * -1, 10);


    this.cursors.up = (deltaY < 0);
    this.cursors.down = (deltaY > 0);
    this.cursors.left = (deltaX < 0);
    this.cursors.right = (deltaX > 0);

     console.log(this.pointer.position.x);

    /*if(deltaX > 0 && prevDeltaX > 0){
      var deltaOfDeltaX = prevDeltaX - deltaX;
      if(deltaOfDeltaX > 15){
          this.cursors.right = true;
          this.cursors.left = false;
      } 
    }else if(deltaX < 0 && prevDeltaX < 0){
      if(deltaOfDeltaX < -15){
          this.cursors.left = true;
          this.cursors.right = false;
      } 
    }*/

    this.imageGroup.forEach(function (e, i) {
      e.cameraOffset.x = initialPoint.x + (deltaX) * i / 3;
      e.cameraOffset.y = initialPoint.y + (deltaY) * i / 3;
    }, this);
  };

  Phaser.Plugin.VJoy.prototype.preUpdate = empty;

}.call(this, window, Phaser));
