/* global Phaser */

(function (Phaser) {
  'use strict';

  var isLeftDown = false;
  var isRightDown = false;
  var isLeftUp = false;
  var isRightUp = false;
  var isThrottleDown = false;
  var isThrottleUp = false;

  var throttleId = 1;
  var rightArrowId = 2;
  var leftArrowId = 3;

  var buttonDown = function(buttonID){};
  var buttonUp = function(buttonID){};

  var throttle = function(){console.log("Throttle")};

  var usernamePrompt = function(){

    console.log("hey");

    var newUsername = prompt("Enter your name! (q to quit)", username);

    if (newUsername == "" || newUsername == username || newUsername == null) {
        return;
    }else if(newUsername == "q"){
        sendToBackend("disconnect");
        return;
    }

    setUsername(newUsername);
    sendToBackend("set username", username);
  };

  var leftDown = function() { 
    if(!isLeftDown){ 
      isLeftDown = true; 
      isLeftUp = false; 
      console.log("leftDown");
      buttonDown(leftArrowId);
    }
  };

  var rightDown = function(){ 
    if(!isRightDown){ 
      isRightDown = true; 
      isRightUp = false;
      console.log("rightDown");
      buttonDown(rightArrowId);
    }
  };

  var leftUp = function(){ 
    if(!isLeftUp){ 
      isLeftUp = true; 
      isLeftDown = false; 
      console.log("leftUp");
      buttonUp(leftArrowId);
    }
  };
  
  var rightUp = function(){ 
    if(!isRightUp){ 
      isRightUp = true; 
      isRightDown = false; 
      console.log("rightUp");
      buttonUp(rightArrowId);
    }
  };

  var throttleDown = function(){ 
    if(!isThrottleDown){ 
      isThrottleDown = true; 
      isThrottleUp = false;
      buttonDown(throttleId);
      redButtons.frame = 1;
    }
  };

  var throttleUp = function(){ 
    if(!isThrottleUp){ 
      isThrottleUp = true; 
      isThrottleDown = false;
      buttonUp(throttleId);
      redButtons.frame = 0;
    }
  };


  var arrowLeft;
  var arrowRight;
  var backgroundArrowLeft;
  var bakcgroundArrowRight;
  var whitePaddle;
  var redButtons;
  var tools_button;

  var GREY = "0xbebbbd";
  var BLACK = "0x111213";
  var WHITE = "0xffffff";
  var RED = "0xD40100";
  
  /*var previousSteeringDirection = 1; // For vibreering. Hvis forrige retning var høyre og man nå tar til venstre --> vibrer
  var directionChangeSound;*/

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

  
    game.load.image('tools_button', 'assets/Tools_button.png');
    game.load.spritesheet('redButtons', 'assets/redButtons.png', 150, 149);
    game.load.spritesheet('arrows', 'assets/arrows-2x.png', 200, 250);
    //game.load.audio('directionChange', 'assets/squit.ogg');

    },

    create: function create() {


      game.stage.backgroundColor = '#EFEFEF';

      game.scale.scaleMode = Phaser.ScaleManager.EXACT_FIT;

      game.scale.fullScreenScaleMode = Phaser.ScaleManager.EXACT_FIT;
      game.scale.startFullScreen(false);
      game.scale.refresh();

      tools_button = game.add.sprite(this.game.width/2.3, this.game.height/6, 'tools_button');
      tools_button.data = 4;
      tools_button.scale.setTo(0.03,0.04);
      tools_button.inputEnabled = true;
      tools_button.events.onInputDown.add(usernamePrompt, this);

      redButtons = game.add.sprite(this.game.width/1.4, this.game.height/4, 'redButtons');
      redButtons.data = 1;
      redButtons.frame = 0;
      redButtons.scale.setTo(1,1.2);
      redButtons.inputEnabled = true;

      var whitePaddleX = 0;
      var whitePaddleY = 0;
      var whitePaddleWidth = game.stage.width/2.5;
      var whitePaddleHeight = game.stage.height;

      var arrowKeysMargin = whitePaddleHeight/200;

      var arrowLeftX = whitePaddleX + arrowKeysMargin;
      var arrowLeftY = whitePaddleY + arrowKeysMargin;
      var arrowLeftWidth = (whitePaddleWidth - 3*arrowKeysMargin)/2;
      var arrowLeftHeight = (whitePaddleHeight - 2*arrowKeysMargin);
      var arrowLeftEndX = arrowLeftX + arrowLeftWidth;
      var arrowLeftEndY = arrowLeftY + arrowLeftHeight;

      var arrowRightX = arrowLeftEndX + arrowKeysMargin;
      var arrowRightY = arrowLeftY;
      var arrowRightWidth = arrowLeftWidth;
      var arrowRightHeight = arrowLeftHeight;
      var arrowRightEndX = arrowRightX + arrowRightWidth;
      var arrowRightEndY = arrowRightY + arrowRightHeight; 

      var middleOfArrowLeftX = (arrowLeftX + arrowLeftEndX)/2;
      var middleOfArrowLeftY = (arrowLeftY + arrowLeftEndY)/2;

      var middleOfArrowRightX = (arrowRightX + arrowRightEndX)/2;
      var middleOfArrowRightY = (arrowRightY + arrowRightEndY)/2;

      whitePaddle = game.add.graphics(0,0);
      whitePaddle.beginFill(WHITE, 1);
      whitePaddle.drawRect(
        whitePaddleX,
        whitePaddleY,
        whitePaddleWidth,
        whitePaddleHeight);
    //Pilene som skal ligge over det hvite området
      arrowLeft = game.add.graphics(0,0);
      arrowLeft.beginFill(BLACK,1);
      arrowLeft.drawRect(
        arrowLeftX,
        arrowLeftY,
        arrowLeftWidth,
        arrowLeftHeight);

      arrowRight = game.add.graphics(0,0);
      arrowRight.beginFill(BLACK,1);
      arrowRight.drawRect(
        arrowRightX,
        arrowRightY,
        arrowRightWidth,
        arrowRightHeight);


      bakcgroundArrowRight = game.add.sprite(0, 0);
      bakcgroundArrowRight.addChild(arrowRight);
      bakcgroundArrowRight.data = 2; // knappID/buttonID
      bakcgroundArrowRight.inputEnabled = true;

      backgroundArrowLeft = game.add.sprite(0, 0);
      backgroundArrowLeft.addChild(arrowLeft);
      backgroundArrowLeft.data = 3; //knappID/buttonID
      backgroundArrowLeft.inputEnabled = true;

      var scale = 0.25;
      var arrowSpriteWidth = 200*scale;
      var arrowSpriteHeight = 250*scale;

      var leftArrowImage = game.add.sprite(middleOfArrowLeftX - arrowSpriteWidth, middleOfArrowLeftY - arrowSpriteHeight, 'arrows');
          leftArrowImage.frame = 0;
          leftArrowImage.scale.setTo(0.5, 0.5);


      var rightArrowImage = game.add.sprite(middleOfArrowRightX - arrowSpriteWidth, middleOfArrowRightY - arrowSpriteHeight, 'arrows');
          rightArrowImage.frame = 1;
          rightArrowImage.scale.setTo(0.5, 0.5);



      backgroundArrowLeft.events.onInputOver.add(leftDown, this);
      backgroundArrowLeft.events.onInputDown.add(leftDown, this);

      bakcgroundArrowRight.events.onInputOver.add(rightDown, this);
      bakcgroundArrowRight.events.onInputDown.add(rightDown, this);

      backgroundArrowLeft.events.onInputOut.add(leftUp, this);
      backgroundArrowLeft.events.onInputUp.add(leftUp, this);

      bakcgroundArrowRight.events.onInputOut.add(rightUp, this);
      bakcgroundArrowRight.events.onInputUp.add(rightUp, this);

      redButtons.events.onInputDown.add(throttleDown, this);
      redButtons.events.onInputOver.add(throttleDown, this);

      redButtons.events.onInputOut.add(throttleUp, this);
      redButtons.events.onInputUp.add(throttleUp, this);

    
    }

  });

}.call(this, Phaser));
