(function (w) {
  'use strict';
  var S = w.TG.store;
  var root, cur, timer;

  var TAGS = ['回消息', '查资料', '放松一下'];
  var ALTS = ['倒杯水', '站起来走两步', '给家里回个电话', '把桌上收拾一下', '写三行今天的笔记', '看一眼窗外'];

  function esc(s) {
    return String(s).replace(/[&<>"]/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c];
    });
  }
  function pad(n) { return String(n).padStart(2, '0'); }

  function shell(mode, html) {
    root.className = 'overlay overlay--' + mode;
    root.hidden = false;
    root.innerHTML = '<div class="panel">' + html + '</div>';
  }

  /* ---------- 第 1 屏：时长弹窗 ---------- */
  function stepDur() {
    cur.min = 5;
    cur.custom = false;

    shell('sheet',
      '<div class="grab"></div>' +
      '<p class="sheet-kicker">' + esc(cur.app.name) + ' · 今天已用 ' + cur.app.used + ' 分钟</p>' +
      '<p class="sheet-title">这次用多久？</p>' +
      '<div class="dur" id="dur">' +
        '<button class="dur__item is-on" data-v="5"><b>5</b><span>分钟</span></button>' +
        '<button class="dur__item" data-v="15"><b>15</b><span>分钟</span></button>' +
        '<button class="dur__item" data-v="custom"><b>自定义</b><span>1–120</span></button>' +
      '</div>' +
      '<div class="custom" id="customBox" hidden>' +
        '<div class="custom__top"><span class="t-3">自定义时长</span><b class="num"><span id="cval">30</span> 分钟</b></div>' +
        '<input class="range" id="crange" type="range" min="1" max="120" value="30">' +
      '</div>' +
      '<button class="btn btn--primary btn--block" id="ok">继续</button>' +
      '<button class="textlink" id="quit">直接退出</button>');

    var box = root.querySelector('#customBox');
    var range = root.querySelector('#crange');
    var cval = root.querySelector('#cval');

    root.querySelectorAll('#dur .dur__item').forEach(function (b) {
      b.addEventListener('click', function () {
        root.querySelectorAll('#dur .dur__item').forEach(function (x) { x.classList.remove('is-on'); });
        b.classList.add('is-on');
        if (b.dataset.v === 'custom') {
          cur.custom = true;
          cur.min = Number(range.value);
          box.hidden = false;
        } else {
          cur.custom = false;
          cur.min = Number(b.dataset.v);
          box.hidden = true;
        }
      });
    });

    range.addEventListener('input', function () {
      cval.textContent = range.value;
      cur.min = Number(range.value);
    });

    root.querySelector('#ok').addEventListener('click', stepIntent);
    root.querySelector('#quit').addEventListener('click', abort);
  }

  /* ---------- 第 2 屏：意图 ---------- */
  function stepIntent() {
    shell('sheet',
      '<div class="grab"></div>' +
      '<p class="sheet-kicker">第 2 步 / 3</p>' +
      '<p class="sheet-title" style="text-align:left">你打开它想做什么？</p>' +
      '<p class="q-sub">写一句，哪怕只是「看看有没有人找我」。</p>' +
      '<div class="chips" id="tags">' +
        TAGS.map(function (t) { return '<button class="chip" data-t="' + t + '">' + t + '</button>'; }).join('') +
      '</div>' +
      '<div class="field"><textarea id="intent" maxlength="40" placeholder="比如：回两条消息就退出"></textarea></div>' +
      '<button class="btn btn--primary btn--block" id="ok" disabled>继续</button>' +
      '<button class="textlink" id="quit">直接退出</button>');

    var ta = root.querySelector('#intent');
    var ok = root.querySelector('#ok');

    function sync() {
      cur.intent = ta.value.trim();
      ok.disabled = cur.intent.length < 2;
    }
    ta.addEventListener('input', sync);
    ta.focus();

    root.querySelectorAll('#tags .chip').forEach(function (c) {
      c.addEventListener('click', function () {
        root.querySelectorAll('#tags .chip').forEach(function (x) { x.classList.remove('is-on'); });
        c.classList.add('is-on');
        ta.value = c.dataset.t === '放松一下' ? '刷一会儿就睡'
                 : c.dataset.t === '查资料'  ? '查一个东西就走'
                 : '回两条消息';
        sync();
        ta.focus();
      });
    });

    ok.addEventListener('click', stepAlt);
    root.querySelector('#quit').addEventListener('click', abort);
  }

  /* ---------- 第 3 屏：替代动作 ---------- */
  function stepAlt() {
    shell('sheet',
      '<div class="grab"></div>' +
      '<p class="sheet-kicker">第 3 步 / 3</p>' +
      '<p class="sheet-title" style="text-align:left">放下它，<br>你能先去做什么？</p>' +
      '<p class="q-sub">挑一件 30 秒能做完的小事。</p>' +
      '<div class="chips" id="alts">' +
        ALTS.map(function (a, i) {
          return '<button class="chip' + (i === 0 ? ' is-on' : '') + '" data-a="' + a + '">' + a + '</button>';
        }).join('') +
      '</div>' +
      '<button class="btn btn--glass btn--block" id="later">我先去做</button>' +
      '<button class="btn btn--primary btn--block" id="anyway">还是想打开</button>' +
      '<button class="textlink" id="quit">直接退出</button>');

    cur.alt = ALTS[0];
    root.querySelectorAll('#alts .chip').forEach(function (c) {
      c.addEventListener('click', function () {
        root.querySelectorAll('#alts .chip').forEach(function (x) { x.classList.remove('is-on'); });
        c.classList.add('is-on');
        cur.alt = c.dataset.a;
      });
    });

    root.querySelector('#later').addEventListener('click', function () {
      var saved = cur.min;
      S.addGate(true);
      close();
      w.TG.toast('省下 ' + saved + ' 分钟');
    });

    root.querySelector('#anyway').addEventListener('click', function () {
      S.addGate(false);
      countdown();
    });

    root.querySelector('#quit').addEventListener('click', abort);
  }

  /* ---------- 计时 ---------- */
  function countdown() {
    var totalSec = cur.min * 60;
    var t0 = Date.now();
    var warned = false;

    shell('full',
      '<div class="ov-top"><span class="ov-step">' + esc(cur.app.name) + '</span></div>' +
      '<div class="count">' +
        '<div class="count__app">正在使用</div>' +
        '<div class="count__num" id="cnum">' + pad(Math.floor(totalSec / 60)) + ':00</div>' +
        '<div class="count__of">承诺 ' + cur.min + ' 分钟</div>' +
        '<p class="count__intent">你写的是「<b>' + esc(cur.intent) + '</b>」</p>' +
        '<p class="count__warn" id="cwarn">还剩 1 分钟</p>' +
      '</div>' +
      '<div class="ov-foot"><button class="textlink" id="quit">提前结束</button></div>');

    var num = root.querySelector('#cnum');
    var warn = root.querySelector('#cwarn');

    function stopEarly() {
      var used = Math.round((Date.now() - t0) / 1000 * S.state.settings.speed / 60);
      clearInterval(timer);
      S.addUse(cur.app.id, Math.max(1, used));
      close();
      w.TG.toast('记下 ' + Math.max(1, used) + ' 分钟');
    }
    root.querySelector('#quit').addEventListener('click', stopEarly);

    function tick() {
      var left = totalSec - (Date.now() - t0) / 1000 * S.state.settings.speed;
      if (left <= 0) {
        num.textContent = '00:00';
        clearInterval(timer);
        timeout();
        return;
      }
      num.textContent = pad(Math.floor(left / 60)) + ':' + pad(Math.floor(left % 60));
      if (left <= 60 && !warned) {
        warned = true;
        warn.classList.add('on');
        w.TG.gate.buzz(18);
      }
    }

    clearInterval(timer);
    timer = setInterval(tick, 250);
    tick();
  }

  /* ---------- 到时 ---------- */
  function timeout() {
    shell('full',
      '<div class="reclaim">' +
        '<p class="reclaim__k">时间到</p>' +
        '<p class="reclaim__t">「' + esc(cur.app.name) + '」的<br/><b>' + cur.min + ' 分钟</b>用完了。</p>' +
        '<div class="reclaim__box"><span>你打开它的理由</span><p>' + esc(cur.intent) + '</p></div>' +
        '<div class="reclaim__box"><span>你本可以先去做</span><p>' + esc(cur.alt) + '</p></div>' +
      '</div>' +
      '<div class="ov-foot">' +
        '<button class="btn btn--primary btn--block" id="exit">退出</button>' +
        '<button class="textlink" id="again">再继续使用一会儿</button>' +
        '<button class="textlink textlink--dim" id="toFocus">转去专注 10 分钟</button>' +
      '</div>');

    root.querySelector('#exit').addEventListener('click', function () {
      S.addUse(cur.app.id, cur.min);
      var name = cur.app.name;
      close();
      w.TG.toast('已退出「' + name + '」');
    });

    root.querySelector('#again').addEventListener('click', function () {
      S.addUse(cur.app.id, cur.min);
      cur.used = cur.min;
      stepDur();
    });

    root.querySelector('#toFocus').addEventListener('click', function () {
      S.addUse(cur.app.id, cur.min);
      var name = cur.app.name;
      close();
      w.TG.focus.start(10, '刚放下' + name);
    });
  }

  function buzz(ms) {
    if (!S.state.settings.haptic) return;
    if (!('vibrate' in navigator)) return;
    try { navigator.vibrate(ms); } catch (e) {}
  }

  function abort() {
    S.addGate(true);
    close();
  }

  function close() {
    clearInterval(timer);
    root.hidden = true;
    root.innerHTML = '';
    root.className = 'overlay';
    cur = null;
    w.TG.app.refresh();
  }

  w.TG = w.TG || {};
  w.TG.gate = {
    init: function () { root = document.getElementById('gate'); },
    open: function (app) {
      cur = { app: app, min: 5, intent: '', alt: '', used: 0 };
      stepDur();
    },
    buzz: buzz
  };
})(window);
