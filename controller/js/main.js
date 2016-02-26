/* global Phaser */

(function (Phaser) {
  'use strict';

  var steeringLeft = false;
  var steeringRight = false;
  var buttonDown = function(buttonID){};
  var buttonUp = function(buttonID){};

  var GREY = "0xbebbbd";
  var BLACK = "0x111213";
  var WHITE = "0xffffff";
  var RED = "0xD40100";
  var redButtons;
  var previousSteeringDirection = 1; // For vibreering. Hvis forrige retning var høyre og man nå tar til venstre --> vibrer
  var directionChangeSound;

  var game = new Phaser.Game(800, 600, Phaser.AUTO, 'phaser-example', {

    preload: function preload() {

      addMessageHandler(function(msg){
        if(msg == "identified"){

          buttonDown = function(buttonID) {
            sendToGame("buttonDown", buttonID);
          }

          buttonUp = function(buttonID){
            sendToGame("buttonUp", buttonID);
          }
        }
    });

    game.load.image('vjoy_base', 'assets/base.png');
    game.load.image('vjoy_body', 'assets/body.png');
    game.load.image('vjoy_cap', 'assets/cap.png');
    game.load.spritesheet('redButtons', 'assets/redButtons.png', 150, 149);
    //game.load.audio('directionChange', 'assets/squit.ogg');

    },

    create: function create() {
      game.stage.backgroundColor = '#EFEFEF';

      game.scale.scaleMode = Phaser.ScaleManager.EXACT_FIT;

      game.scale.fullScreenScaleMode = Phaser.ScaleManager.EXACT_FIT;
      game.scale.startFullScreen(false);
      game.scale.refresh();



      game.vjoy = game.plugins.add(Phaser.Plugin.VJoy);
      game.vjoy.inputEnable();

      game.input.onDown.add(function(pointer) {    //Reagerer på all trykk ned(onDown) på game området ==> game.input
        var data;
      
        if (pointer.targetObject) {                // Hvis trykk skjer på en knapp ==> pointer.targetObject == true;
          data = pointer.targetObject.sprite.data; // Henter data(knappID/buttonID) fra knappen som har blitt trykket.
          if(data == 1){
            redButtons.frame = 1;
          }
          buttonDown(data);
        }
      }, this);

      game.input.onUp.add(function(pointer) {
        var data;

        if(pointer.x > game.world.centerX){   //Noen ganger sklir fing av gass-knapp, men  Slipper man da opp fingen, skjer et problem;
          redButtons.frame = 0;                 
          buttonUp(1);
        }

        if (pointer.targetObject) {
          data = pointer.targetObject.sprite.data;
          buttonUp(data);
        }
      }, this);

      //directionChangeSound = game.add.audio('directionChange');

      redButtons = game.add.sprite(this.game.width/1.4, this.game.height/4, 'redButtons');
      redButtons.data = 1;
      redButtons.frame = 0;
      redButtons.scale.setTo(1.3,1.4);
      redButtons.inputEnabled = true;
      },

    update: function update() {
      var cursors = game.vjoy.cursors;

      if (cursors.left) {
        if(!steeringLeft){
          buttonDown(3);
          steeringLeft = true;

          if(previousSteeringDirection == 0){
            //window.navigator.vibrate(100);
            //directionChangeSound.play();
            previousSteeringDirection = 1;
          }
        }
      }else if(steeringLeft){
        buttonUp(3);
        steeringLeft = false;
      }

      if (cursors.right) {
        if(!steeringRight){
          buttonDown(2);
          steeringRight = true;

          if(previousSteeringDirection == 1){
            //directionChangeSound.play();
            //window.navigator.vibrate(100);
            previousSteeringDirection = 0;
          }
        }
      }else if(steeringRight){
        buttonUp(2);
        steeringRight = false;
      }
    }
  });

}.call(this, Phaser));
