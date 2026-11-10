(function (w) {
  'use strict';

  var KEY = 'tg.state.v2';
  var DAY_RESET_HOUR = 4;

  var PRESETS = [
    { id: 'douyin', name: '抖音',     short: '抖', quota: 60 },
    { id: 'xhs',    name: '小红书',   short: '红', quota: 30 },
    { id: 'bili',   name: '哔哩哔哩', short: 'B', quota: 30 },
    { id: 'weibo',  name: '微博',     short: '微', quota: 20 },
    { id: 'game',   name: '王者荣耀', short: '王', quota: 45 }
  ];

  function dayKey(d) {
    var t = new Date((d ? d.getTime() : Date.now()) - DAY_RESET_HOUR * 3600e3);
    var p = function (n) { return String(n).padStart(2, '0'); };
    return t.getFullYear() + '-' + p(t.getMonth() + 1) + '-' + p(t.getDate());
  }

  function fmt(min) {
    min = Math.max(0, Math.round(min));
    if (min < 60) return min + 'm';
    var h = Math.floor(min / 60), r = min % 60;
    return r ? h + 'h ' + r + 'm' : h + 'h';
  }

  function seed() {
    var now = Date.now();
    var used = [41, 18, 12, 6, 0];
    var apps = PRESETS.map(function (p, i) {
      return { id: p.id, name: p.name, short: p.short, quota: p.quota, used: used[i], on: true };
    });
    var history = {};
    var totals = [96, 112, 78, 134, 118, 100];
    for (var i = 6; i >= 1; i--) {
      history[dayKey(new Date(now - i * 864e5))] = totals[6 - i];
    }
    history[dayKey()] = apps.reduce(function (s, a) { return s + a.used; }, 0);

    return {
      v: 2,
      settings: { theme: 'dark', glass: 60, speed: 1, haptic: true, onboarded: false },
      apps: apps,
      history: history,
      gates: { total: 42, abandoned: 11 },
      focus: { count: 2, min: 50 }
    };
  }

  function load() {
    try {
      var raw = localStorage.getItem(KEY);
      if (!raw) throw 0;
      var s = JSON.parse(raw);
      if (!s || s.v !== 2) throw 0;
      return s;
    } catch (e) {
      var fresh = seed();
      try { localStorage.setItem(KEY, JSON.stringify(fresh)); } catch (e2) {}
      return fresh;
    }
  }

  var state = load();

  function save() {
    try { localStorage.setItem(KEY, JSON.stringify(state)); }
    catch (e) { w.TG.toast && w.TG.toast('存储空间不足'); }
  }

  function getApp(id) {
    return state.apps.filter(function (a) { return a.id === id; })[0] || null;
  }

  function totalUsed() {
    return state.apps.reduce(function (s, a) { return s + (a.on ? a.used : 0); }, 0);
  }

  function addUse(id, minutes) {
    var a = getApp(id);
    if (!a) return;
    a.used = Math.max(0, a.used + Math.round(minutes));
    state.history[dayKey()] = totalUsed();
    save();
  }

  function addGate(abandoned) {
    state.gates.total++;
    if (abandoned) state.gates.abandoned++;
    save();
  }

  function addFocus(min) {
    state.focus.count++;
    state.focus.min += Math.round(min);
    save();
  }

  function last7() {
    var out = [];
    for (var i = 6; i >= 0; i--) {
      var d = new Date(Date.now() - i * 864e5);
      var k = dayKey(d);
      out.push({
        key: k,
        min: state.history[k] || 0,
        label: ['日', '一', '二', '三', '四', '五', '六'][d.getDay()],
        today: i === 0
      });
    }
    return out;
  }

  w.TG = w.TG || {};
  w.TG.store = {
    get state() { return state; },
    save: save,
    seed: seed,
    reset: function () {
      state = seed();
      try { localStorage.setItem(KEY, JSON.stringify(state)); } catch (e) {}
      return state;
    },
    replace: function (next) { state = next; save(); return state; },
    PRESETS: PRESETS,
    dayKey: dayKey,
    fmt: fmt,
    getApp: getApp,
    totalUsed: totalUsed,
    addUse: addUse,
    addGate: addGate,
    addFocus: addFocus,
    last7: last7
  };
})(window);
