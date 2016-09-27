var anatomogramBuilder=webpackJsonp_name_([0],{0:/*!******************!*\
  !*** ./index.js ***!
  \******************/
function(e,t,s){"use strict";e.exports=s(/*! ./src/anatomogramRenderer.js */1)},1:/*!************************************!*\
  !*** ./src/anatomogramRenderer.js ***!
  \************************************/
function(e,t,s){"use strict";var n=s(/*! anatomogram */2),r=s(/*! react-dom */159),i=s(/*! events */213),o=new i;t.eventEmitter=o,t.render=function(e){var t=e.species,s=void 0===t?"mus musculus":t,i=e.highlightIDs,h=void 0===i?[]:i,u=e.expressedTissueColour,l=void 0===u?"red":u,v=e.hoveredTissueColour,a=void 0===v?"purple":v,f=e.mountNode,c={pathToFolderWithBundledResources:"..",anatomogramData:{species:s},expressedTissueColour:l,hoveredTissueColour:a,eventEmitter:o,idsToBeHighlighted:h};r.render(n.create(c),document.getElementById(f))}},213:/*!****************************!*\
  !*** ./~/events/events.js ***!
  \****************************/
function(e,t){"use strict";function s(){this._events=this._events||{},this._maxListeners=this._maxListeners||void 0}function n(e){return"function"==typeof e}function r(e){return"number"==typeof e}function i(e){return"object"===("undefined"==typeof e?"undefined":h(e))&&null!==e}function o(e){return void 0===e}var h="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol?"symbol":typeof e};e.exports=s,s.EventEmitter=s,s.prototype._events=void 0,s.prototype._maxListeners=void 0,s.defaultMaxListeners=10,s.prototype.setMaxListeners=function(e){if(!r(e)||e<0||isNaN(e))throw TypeError("n must be a positive number");return this._maxListeners=e,this},s.prototype.emit=function(e){var t,s,r,h,u,l;if(this._events||(this._events={}),"error"===e&&(!this._events.error||i(this._events.error)&&!this._events.error.length)){if(t=arguments[1],t instanceof Error)throw t;var v=new Error('Uncaught, unspecified "error" event. ('+t+")");throw v.context=t,v}if(s=this._events[e],o(s))return!1;if(n(s))switch(arguments.length){case 1:s.call(this);break;case 2:s.call(this,arguments[1]);break;case 3:s.call(this,arguments[1],arguments[2]);break;default:h=Array.prototype.slice.call(arguments,1),s.apply(this,h)}else if(i(s))for(h=Array.prototype.slice.call(arguments,1),l=s.slice(),r=l.length,u=0;u<r;u++)l[u].apply(this,h);return!0},s.prototype.addListener=function(e,t){var r;if(!n(t))throw TypeError("listener must be a function");return this._events||(this._events={}),this._events.newListener&&this.emit("newListener",e,n(t.listener)?t.listener:t),this._events[e]?i(this._events[e])?this._events[e].push(t):this._events[e]=[this._events[e],t]:this._events[e]=t,i(this._events[e])&&!this._events[e].warned&&(r=o(this._maxListeners)?s.defaultMaxListeners:this._maxListeners,r&&r>0&&this._events[e].length>r&&(this._events[e].warned=!0,console.error("(node) warning: possible EventEmitter memory leak detected. %d listeners added. Use emitter.setMaxListeners() to increase limit.",this._events[e].length),"function"==typeof console.trace&&console.trace())),this},s.prototype.on=s.prototype.addListener,s.prototype.once=function(e,t){function s(){this.removeListener(e,s),r||(r=!0,t.apply(this,arguments))}if(!n(t))throw TypeError("listener must be a function");var r=!1;return s.listener=t,this.on(e,s),this},s.prototype.removeListener=function(e,t){var s,r,o,h;if(!n(t))throw TypeError("listener must be a function");if(!this._events||!this._events[e])return this;if(s=this._events[e],o=s.length,r=-1,s===t||n(s.listener)&&s.listener===t)delete this._events[e],this._events.removeListener&&this.emit("removeListener",e,t);else if(i(s)){for(h=o;h-- >0;)if(s[h]===t||s[h].listener&&s[h].listener===t){r=h;break}if(r<0)return this;1===s.length?(s.length=0,delete this._events[e]):s.splice(r,1),this._events.removeListener&&this.emit("removeListener",e,t)}return this},s.prototype.removeAllListeners=function(e){var t,s;if(!this._events)return this;if(!this._events.removeListener)return 0===arguments.length?this._events={}:this._events[e]&&delete this._events[e],this;if(0===arguments.length){for(t in this._events)"removeListener"!==t&&this.removeAllListeners(t);return this.removeAllListeners("removeListener"),this._events={},this}if(s=this._events[e],n(s))this.removeListener(e,s);else if(s)for(;s.length;)this.removeListener(e,s[s.length-1]);return delete this._events[e],this},s.prototype.listeners=function(e){var t;return t=this._events&&this._events[e]?n(this._events[e])?[this._events[e]]:this._events[e].slice():[]},s.prototype.listenerCount=function(e){if(this._events){var t=this._events[e];if(n(t))return 1;if(t)return t.length}return 0},s.listenerCount=function(e,t){return e.listenerCount(t)}}});