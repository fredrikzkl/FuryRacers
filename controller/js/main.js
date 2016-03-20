/* global Phaser */

function setBackgroundColor(color){

  game.stage.backgroundColor = color;
}

var game;

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

  var arrowLeftBlackArea;
  var arrowRightBlackArea;
  var backgroundArrowLeftBlackArea;
  var bakcgroundArrowRightBlackArea;
  var whitePaddleBackground;
  var redButtons;
  var tools_button;

  var GREY = "0xbebbbd";
  var BLACK = "0x111213";
  var WHITE = "0xffffff";
  var RED = "0xD40100";
  var offWHITE = '#EFEFEF';

  var buttonDown = function(buttonID){};
  var buttonUp = function(buttonID){};

  var throttle = function(){console.log("Throttle")};

 

  var usernamePrompt = function(){  

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

  function createThrottleButton(dis){

    redButtons = game.add.sprite(dis.game.width/1.4, dis.game.height/4, 'redButtons');
    redButtons.data = 1;
    redButtons.frame = 0;
    redButtons.scale.setTo(1,1.2);
    redButtons.inputEnabled = true;
  }

  function createToolsButton(dis){

    tools_button = game.add.sprite(dis.game.width/2.3, dis.game.height/6, 'tools_button');
    tools_button.data = 4;
    tools_button.scale.setTo(0.03,0.04);
    tools_button.inputEnabled = true;
  }

  function createButtonEvents(game){

    tools_button.events.onInputDown.add(usernamePrompt, game);

    backgroundArrowLeftBlackArea.events.onInputOver.add(leftDown, game);
    backgroundArrowLeftBlackArea.events.onInputDown.add(leftDown, game);

    bakcgroundArrowRightBlackArea.events.onInputOver.add(rightDown, game);
    bakcgroundArrowRightBlackArea.events.onInputDown.add(rightDown, game);

    backgroundArrowLeftBlackArea.events.onInputOut.add(leftUp, game);
    backgroundArrowLeftBlackArea.events.onInputUp.add(leftUp, game);

    bakcgroundArrowRightBlackArea.events.onInputOut.add(rightUp, game);
    bakcgroundArrowRightBlackArea.events.onInputUp.add(rightUp, game);

    redButtons.events.onInputDown.add(throttleDown, game);
    redButtons.events.onInputOver.add(throttleDown, game);

    redButtons.events.onInputOut.add(throttleUp, game);
    redButtons.events.onInputUp.add(throttleUp, game);
  }

    game = new Phaser.Game(800, 600, Phaser.AUTO, 'controller', {

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

    create: function create(){

      setBackgroundColor(offWHITE, game);

      game.scale.scaleMode = Phaser.ScaleManager.EXACT_FIT;
      game.scale.fullScreenScaleMode = Phaser.ScaleManager.EXACT_FIT;
      game.scale.startFullScreen(false);
      game.scale.refresh();

      createToolsButton(this);
      createThrottleButton(this);
     
      var whitePaddleBackgroundX = 0;
      var whitePaddleBackgroundY = 0;
      var whitePaddleBackgroundWidth = game.stage.width/2.5;
      var whitePaddleBackgroundHeight = game.stage.height;

      whitePaddleBackground = game.add.graphics(0,0);
      whitePaddleBackground.beginFill(WHITE, 1);
      whitePaddleBackground.drawRect(
        whitePaddleBackgroundX,
        whitePaddleBackgroundY,
        whitePaddleBackgroundWidth,
        whitePaddleBackgroundHeight);

      var arrowKeysMargin = whitePaddleBackgroundHeight/200;

      var arrowLeftBlackAreaX = whitePaddleBackgroundX + arrowKeysMargin;
      var arrowLeftBlackAreaY = whitePaddleBackgroundY + arrowKeysMargin;
      var arrowLeftBlackAreaWidth = (whitePaddleBackgroundWidth - 3*arrowKeysMargin)/2;
      var arrowLeftBlackAreaHeight = (whitePaddleBackgroundHeight - 2*arrowKeysMargin);
      var arrowLeftBlackAreaEndX = arrowLeftBlackAreaX + arrowLeftBlackAreaWidth;
      var arrowLeftBlackAreaEndY = arrowLeftBlackAreaY + arrowLeftBlackAreaHeight;

      var arrowRightBlackAreaX = arrowLeftBlackAreaEndX + arrowKeysMargin;
      var arrowRightBlackAreaY = arrowLeftBlackAreaY;
      var arrowRightBlackAreaWidth = arrowLeftBlackAreaWidth;
      var arrowRightBlackAreaHeight = arrowLeftBlackAreaHeight;
      var arrowRightBlackAreaEndX = arrowRightBlackAreaX + arrowRightBlackAreaWidth;
      var arrowRightBlackAreaEndY = arrowRightBlackAreaY + arrowRightBlackAreaHeight; 

      var middleOfArrowLeftBlackAreaX = (arrowLeftBlackAreaX + arrowLeftBlackAreaEndX)/2;
      var middleOfArrowLeftBlackAreaY = (arrowLeftBlackAreaY + arrowLeftBlackAreaEndY)/2;

      var middleOfArrowRightBlackAreaX = (arrowRightBlackAreaX + arrowRightBlackAreaEndX)/2;
      var middleOfArrowRightBlackAreaY = (arrowRightBlackAreaY + arrowRightBlackAreaEndY)/2;

     
    //Pilene som skal ligge over det hvite området
      arrowLeftBlackArea = game.add.graphics(0,0);
      arrowLeftBlackArea.beginFill(BLACK,1);
      arrowLeftBlackArea.drawRect(
        arrowLeftBlackAreaX,
        arrowLeftBlackAreaY,
        arrowLeftBlackAreaWidth,
        arrowLeftBlackAreaHeight);

      arrowRightBlackArea = game.add.graphics(0,0);
      arrowRightBlackArea.beginFill(BLACK,1);
      arrowRightBlackArea.drawRect(
        arrowRightBlackAreaX,
        arrowRightBlackAreaY,
        arrowRightBlackAreaWidth,
        arrowRightBlackAreaHeight);


      bakcgroundArrowRightBlackArea = game.add.sprite(0, 0);
      bakcgroundArrowRightBlackArea.addChild(arrowRightBlackArea);
      bakcgroundArrowRightBlackArea.data = 2; // knappID/buttonID
      bakcgroundArrowRightBlackArea.inputEnabled = true;

      backgroundArrowLeftBlackArea = game.add.sprite(0, 0);
      backgroundArrowLeftBlackArea.addChild(arrowLeftBlackArea);
      backgroundArrowLeftBlackArea.data = 3; //knappID/buttonID
      backgroundArrowLeftBlackArea.inputEnabled = true;

      var arrowImageScale = 0.25;
      var arrowSpriteWidth = 200*arrowImageScale;
      var arrowSpriteHeight = 250*arrowImageScale;

      var leftArrowImage = game.add.sprite(middleOfArrowLeftBlackAreaX - arrowSpriteWidth, middleOfArrowLeftBlackAreaY - arrowSpriteHeight, 'arrows');
          leftArrowImage.frame = 0;
          leftArrowImage.scale.setTo(0.5, 0.5);


      var rightArrowImage = game.add.sprite(middleOfArrowRightBlackAreaX - arrowSpriteWidth, middleOfArrowRightBlackAreaY - arrowSpriteHeight, 'arrows');
          rightArrowImage.frame = 1;
          rightArrowImage.scale.setTo(0.5, 0.5);

      createButtonEvents(this);
    
    }

  });

}.call(this, Phaser));
