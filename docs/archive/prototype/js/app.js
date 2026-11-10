(function (w) {
  'use strict';
  var S = w.TG.store;
  var $ = function (s) { return document.querySelector(s); };
  var $$ = function (s) { return Array.prototype.slice.call(document.querySelectorAll(s)); };

  var RAMP = ['#5B6CFF', '#7B88FF', '#9AA4FF', '#B9C0FF', '#D8DCFF'];

  /* ---------- toast ---------- */
  var toastEl, toastTimer;
  function toast(msg) {
    if (!toastEl) {
      toastEl = document.createElement('div');
      toastEl.className = 'toast';
      document.body.appendChild(toastEl);
    }
    toastEl.textContent = msg;
    toastEl.classList.add('on');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(function () { toastEl.classList.remove('on'); }, 1800);
  }
  w.TG.toast = toast;

  /* ---------- 主题 ---------- */
  var mq = w.matchMedia('(prefers-color-scheme: dark)');
  function applyTheme() {
    var t = S.state.settings.theme;
    var real = t === 'system' ? (mq.matches ? 'dark' : 'light') : t;
    document.documentElement.dataset.theme = real;
    var meta = document.querySelector('meta[name="theme-color"]');
    if (meta) meta.setAttribute('content', real === 'dark' ? '#000000' : '#F2F2F7');
    var seg = $('#themeSeg');
    if (seg) $$('#themeSeg button').forEach(function (b) { b.classList.toggle('is-on', b.dataset.v === t); });
  }
  if (mq.addEventListener) mq.addEventListener('change', function () {
    if (S.state.settings.theme === 'system') applyTheme();
  });

  function applyGlass() {
    var v = Number(S.state.settings.glass);
    document.documentElement.style.setProperty('--glass-blur', (4 + v * 0.28).toFixed(0) + 'px');
    document.documentElement.style.setProperty('--glass-alpha', (0.85 - v * 0.005).toFixed(2));
    var out = $('#glassVal');
    if (out) out.textContent = v;
  }

  /* ---------- 导航 ---------- */
  function go(name) {
    $$('.screen').forEach(function (s) { s.classList.toggle('is-active', s.dataset.screen === name); });
    var idx = 0;
    $$('.tab').forEach(function (t, i) {
      var on = t.dataset.tab === name;
      t.classList.toggle('is-active', on);
      t.setAttribute('aria-selected', on ? 'true' : 'false');
      if (on) idx = i;
    });
    $('#ind').style.transform = 'translateX(' + (idx * 100) + '%)';
    $('#screens').scrollTop = 0;
    $('#tabbar').classList.remove('is-compact');
  }

  /* ---------- 今日 ---------- */
  function renderHero() {
    var apps = S.state.apps.filter(function (a) { return a.on && a.used > 0; })
      .slice().sort(function (a, b) { return b.used - a.used; });
    var total = S.totalUsed();
    var C = 2 * Math.PI * 86;
    var acc = 0, svg = '', leg = '';

    if (total > 0) {
      apps.forEach(function (a, i) {
        var len = C * (a.used / total);
        var dash = Math.max(2, len - 16);
        svg += '<circle cx="100" cy="100" r="86" fill="none" stroke="' + RAMP[i % 5] +
          '" stroke-width="9" stroke-linecap="round" stroke-dasharray="' + dash.toFixed(1) + ' ' + (C - dash).toFixed(1) +
          '" stroke-dashoffset="' + (-acc).toFixed(1) + '" transform="rotate(-90 100 100)"/>';
        acc += len;
        leg += '<span><i style="background:' + RAMP[i % 5] + '"></i>' + a.name + '</span>';
      });
    }

    $('#donut').innerHTML = svg;
    $('#legend').innerHTML = leg;
    $('#ringNum').textContent = S.fmt(total);

    var y = S.state.history[S.dayKey(new Date(Date.now() - 864e5))];
    var d = $('#heroDelta');
    if (y && total) {
      var diff = Math.round(y - total);
      d.textContent = '比昨天' + (diff >= 0 ? '少 ' : '多 ') + Math.abs(diff) + ' 分钟';
    } else {
      d.textContent = total ? '今天刚开始记' : '今天还没有记录';
    }

    var top = S.state.apps.filter(function (a) { return a.on; })
      .slice().sort(function (a, b) { return b.used - a.used; })[0];
    if (top) {
      $('#topAppName').textContent = top.name;
      $('#topAppMeta').textContent = '今日 ' + top.used + ' 分钟 · 上限 ' + top.quota;
      $('#focusCard').dataset.id = top.id;
    }
  }

  /* ---------- 应用 ---------- */
  function renderApps() {
    var list = $('#appList');
    list.innerHTML = S.state.apps.filter(function (a) { return a.on; }).map(function (a) {
      var pct = a.quota ? Math.min(100, a.used / a.quota * 100) : 0;
      return '<button class="app-row" data-id="' + a.id + '">' +
        '<span class="app-ico">' + a.short + '</span>' +
        '<span class="app-main">' +
          '<span class="app-top"><span class="app-name">' + a.name + '</span>' +
          '<span class="app-use num">' + a.used + ' / ' + a.quota + '</span></span>' +
          '<span class="app-bar"><i style="width:' + pct.toFixed(0) + '%"></i></span>' +
        '</span></button>';
    }).join('');

    $$('.app-row').forEach(function (row) {
      row.addEventListener('click', function () {
        var app = S.getApp(row.dataset.id);
        if (app) w.TG.gate.open(app);
      });
      row.addEventListener('contextmenu', function (e) {
        e.preventDefault();
        var app = S.getApp(row.dataset.id);
        var name = prompt('改名', app.name);
        if (name && name.trim()) { app.name = name.trim().slice(0, 8); S.save(); refresh(); }
      });
    });

    $('#appsCount').textContent = S.state.apps.filter(function (a) { return a.on; }).length + ' 个 · 点一行进入闸门';
  }

  /* ---------- 统计 ---------- */
  function renderStats() {
    var days = S.last7();
    var max = Math.max.apply(null, days.map(function (d) { return d.min; }).concat([1]));

    $('#chart').innerHTML = days.map(function (d, i) {
      var h = Math.max(3, d.min / max * 100);
      return '<div class="chart__col' + (d.today ? ' is-today' : '') + '">' +
        '<div class="chart__bar" style="height:' + h + '%;animation-delay:' + (i * 45) + 'ms"></div></div>';
    }).join('');

    $('#chartAxis').innerHTML = days.map(function (d) {
      return '<span>' + (d.today ? '今' : d.label) + '</span>';
    }).join('');

    var apps = S.state.apps.slice().sort(function (a, b) { return b.used - a.used; });
    var top = Math.max.apply(null, apps.map(function (a) { return a.used; }).concat([1]));
    $('#rank').innerHTML = apps.map(function (a) {
      return '<div class="rank-row"><span class="rank-name">' + a.name + '</span>' +
        '<span class="rank-bar"><i style="width:' + (a.used / top * 100).toFixed(0) + '%"></i></span>' +
        '<span class="rank-val num">' + a.used + 'm</span></div>';
    }).join('');

    var g = S.state.gates;
    var rate = g.total ? Math.round(g.abandoned / g.total * 100) : 0;
    $('#funnel').innerHTML =
      '<div class="funnel__row"><span class="t-3">打开受管应用</span><span class="num">' + g.total + ' 次</span></div>' +
      '<div class="funnel__row"><span class="t-3">写下理由并通过闸门</span><span class="num">' + g.total + ' 次</span></div>' +
      '<div class="funnel__big num">' + rate + '%</div>' +
      '<p class="t-3" style="margin-bottom:10px">在闸门里选择「我先去做」而没打开</p>' +
      '<div class="funnel__track"><i style="width:' + rate + '%"></i></div>';
  }

  /* ---------- 我的 ---------- */
  function renderMe() {
    var st = S.state.settings;
    $('#glassRange').value = st.glass;
    $('#glassVal').textContent = st.glass;
    $('#haptic').checked = !!st.haptic;
    $$('#themeSeg button').forEach(function (b) { b.classList.toggle('is-on', b.dataset.v === st.theme); });
    $$('#speedSeg button').forEach(function (b) { b.classList.toggle('is-on', Number(b.dataset.v) === st.speed); });
  }

  function refresh() {
    renderHero();
    renderApps();
    renderStats();
    renderMe();
  }

  /* ---------- 专注 ---------- */
  var focusTimer;
  w.TG.focus = {
    start: function (minutes, task) {
      var el = document.getElementById('focus');
      var total = minutes * 60;
      var t0 = Date.now();
      el.className = 'overlay overlay--full';
      el.hidden = false;

      function fmtT(s) {
        s = Math.max(0, Math.ceil(s));
        return String(Math.floor(s / 60)).padStart(2, '0') + ':' + String(s % 60).padStart(2, '0');
      }

      el.innerHTML = '<div class="panel">' +
        '<div class="ov-top"><span class="ov-step">专注 · ' + minutes + ' 分钟</span></div>' +
        '<div class="focus-inner">' +
          '<div class="focus-time" id="fxTime">' + fmtT(total) + '</div>' +
          '<div class="focus-task">' + (task || '什么都不做') + '</div>' +
          '<p class="focus-note">这期间受管应用都不该打开。<br/>走神了就回来，不用自责。</p>' +
        '</div>' +
        '<div class="ov-foot"><button class="textlink" id="fxQuit">放弃专注</button></div>' +
        '</div>';

      function stop() {
        clearInterval(focusTimer);
        el.hidden = true;
        el.innerHTML = '';
      }
      el.querySelector('#fxQuit').addEventListener('click', stop);

      function tick() {
        var left = total - (Date.now() - t0) / 1000 * S.state.settings.speed;
        var node = el.querySelector('#fxTime');
        if (!node) return;
        if (left <= 0) {
          stop();
          S.addFocus(minutes);
          w.TG.gate.buzz(30);
          toast('完成 ' + minutes + ' 分钟专注');
          refresh();
          return;
        }
        node.textContent = fmtT(left);
      }
      clearInterval(focusTimer);
      focusTimer = setInterval(tick, 250);
    }
  };

  /* ---------- 首次引导 ---------- */
  function showOnboard() {
    var el = $('#onboard');
    var step = 0;
    var perms = [false, false, false];
    var picked = {};
    S.state.apps.slice(0, 3).forEach(function (a) { picked[a.id] = true; });
    el.hidden = false;

    var PERMS = [
      { n: '使用情况访问', d: '读取每个应用今天用了多久。系统设置里手动开启。' },
      { n: '显示在其他应用上层', d: '时间到了，能盖在别的应用上面提醒你。' },
      { n: '后台运行', d: '锁屏或切走之后，计时不中断。' }
    ];

    function dots(i) {
      return '<div class="ob__dots">' + [0, 1, 2].map(function (k) {
        return '<i class="' + (k === i ? 'on' : '') + '"></i>';
      }).join('') + '</div>';
    }

    function render() {
      if (step === 0) {
        el.innerHTML =
          '<div class="ob__body">' +
            '<p class="ob__mark">闸</p>' +
            '<h1 class="ob__h">打开之前，<br/>先想一下。</h1>' +
            '<p class="ob__p">每次打开受管应用，先问你三件事：为什么打开、用多久、放下它你能先去做什么。</p>' +
            '<p class="ob__p">所有数据只存在这台设备上。不联网、不上传、不需要账号。</p>' +
          '</div>' +
          '<div class="ob__foot">' + dots(0) +
            '<button class="btn btn--primary btn--block" id="next">开始设置</button></div>';
        el.querySelector('#next').onclick = function () { step = 1; render(); };
        return;
      }

      if (step === 1) {
        el.innerHTML =
          '<div class="ob__body">' +
            '<h1 class="ob__h">需要三项能力</h1>' +
            '<p class="ob__p">缺任何一项，闸门都拦不住。这是演示，点一下就当授权了。</p>' +
            '<div class="card" style="margin-top:20px">' +
              PERMS.map(function (p, i) {
                return '<div class="perm"><div class="perm__t"><div class="perm__n">' + p.n + '</div>' +
                  '<div class="perm__d">' + p.d + '</div></div>' +
                  '<button class="btn btn--glass btn--xs" data-p="' + i + '">' + (perms[i] ? '已开启' : '开启') + '</button></div>';
              }).join('') +
            '</div>' +
          '</div>' +
          '<div class="ob__foot">' + dots(1) +
            '<button class="btn btn--primary btn--block" id="next"' + (perms[0] && perms[1] && perms[2] ? '' : ' disabled') + '>继续</button></div>';
        el.querySelectorAll('[data-p]').forEach(function (b) {
          b.onclick = function () { perms[Number(b.dataset.p)] = true; render(); };
        });
        var nx = el.querySelector('#next');
        if (nx) nx.onclick = function () { step = 2; render(); };
        return;
      }

      el.innerHTML =
        '<div class="ob__body">' +
          '<h1 class="ob__h">先管住哪几个？</h1>' +
          '<p class="ob__p">不必贪多。选你今天最想放下的那几个。</p>' +
          '<div class="card" style="margin-top:18px">' +
            S.state.apps.map(function (a) {
              return '<div class="pick' + (picked[a.id] ? ' on' : '') + '" data-a="' + a.id + '">' +
                '<span class="app-ico" style="width:30px;height:30px;border-radius:8px;font-size:12px">' + a.short + '</span>' +
                '<span class="pick__name">' + a.name + '</span>' +
                '<span class="pick__use">' + a.used + ' 分钟</span>' +
                '<span class="pick__ck">✓</span></div>';
            }).join('') +
          '</div>' +
        '</div>' +
        '<div class="ob__foot">' + dots(2) +
          '<button class="btn btn--primary btn--block" id="next">开始使用</button></div>';

      el.querySelectorAll('.pick').forEach(function (row) {
        row.onclick = function () {
          var id = row.dataset.a;
          var count = Object.keys(picked).filter(function (k) { return picked[k]; }).length;
          if (picked[id] && count <= 1) return toast('至少留一个');
          picked[id] = !picked[id];
          row.classList.toggle('on', picked[id]);
        };
      });

      el.querySelector('#next').onclick = function () {
        S.state.apps.forEach(function (a) { a.on = !!picked[a.id]; });
        S.state.settings.onboarded = true;
        S.save();
        el.hidden = true;
        refresh();
        go('today');
        toast('配置完成');
      };
    }

    render();
  }

  /* ---------- 事件 ---------- */
  function bind() {
    $$('.tab').forEach(function (t) {
      t.addEventListener('click', function () { go(t.dataset.tab); });
    });

    $('#topAppOpen').addEventListener('click', function () {
      var app = S.getApp($('#focusCard').dataset.id);
      if (app) w.TG.gate.open(app);
    });

    $('#pickApp').addEventListener('click', function () { go('apps'); });

    $('#focusStart').addEventListener('click', function () {
      w.TG.focus.start(25, '先做眼前这一件事');
    });

    $('#addApp').addEventListener('click', function () {
      var off = S.state.apps.filter(function (a) { return !a.on; })[0];
      if (!off) return toast('内置应用都加上了');
      off.on = true;
      S.save(); refresh();
      toast('已加回「' + off.name + '」');
    });

    $$('#themeSeg button').forEach(function (b) {
      b.addEventListener('click', function () {
        S.state.settings.theme = b.dataset.v;
        S.save(); applyTheme();
      });
    });

    $('#glassRange').addEventListener('input', function (e) {
      S.state.settings.glass = Number(e.target.value);
      $('#glassVal').textContent = e.target.value;
      applyGlass(); S.save();
    });

    $('#haptic').addEventListener('change', function (e) {
      S.state.settings.haptic = e.target.checked;
      S.save();
    });

    $$('#speedSeg button').forEach(function (b) {
      b.addEventListener('click', function () {
        S.state.settings.speed = Number(b.dataset.v);
        S.save(); renderMe();
        toast('时间加速 ' + b.dataset.v + '×');
      });
    });

    $('#reseed').addEventListener('click', function () {
      var wasOn = S.state.settings.onboarded;
      var next = S.seed();
      next.settings.onboarded = wasOn;
      S.replace(next);
      applyTheme(); applyGlass(); refresh();
      toast('演示数据已重生成');
    });

    $('#wipe').addEventListener('click', function () {
      if (!confirm('清空所有记录，回到初始状态？')) return;
      S.reset();
      applyTheme(); applyGlass(); refresh();
      go('today');
      toast('已重置');
    });

    $('#themeBtn').addEventListener('click', function () {
      var order = ['dark', 'light', 'system'];
      var i = order.indexOf(S.state.settings.theme);
      S.state.settings.theme = order[(i + 1) % 3];
      S.save(); applyTheme();
    });

    var bar = $('#tabbar');
    $('#screens').addEventListener('scroll', function () {
      bar.classList.toggle('is-compact', this.scrollTop > 10);
    }, { passive: true });

    document.addEventListener('pointermove', function (e) {
      var t = e.target;
      var btn = t && t.closest ? t.closest('.btn') : null;
      if (!btn) return;
      var r = btn.getBoundingClientRect();
      btn.style.setProperty('--mx', ((e.clientX - r.left) / r.width * 100).toFixed(1) + '%');
      btn.style.setProperty('--my', ((e.clientY - r.top) / r.height * 100).toFixed(1) + '%');
    }, { passive: true });
  }

  /* ---------- 启动 ---------- */
  function init() {
    var now = new Date();
    var week = ['日', '一', '二', '三', '四', '五', '六'][now.getDay()];
    $('#todayDate').textContent = (now.getMonth() + 1) + ' 月 ' + now.getDate() + ' 日 · 周' + week;

    w.TG.gate.init();
    applyTheme();
    applyGlass();
    bind();
    refresh();
    go('today');
    w.TG.app = { refresh: refresh, go: go };

    if (location.search.indexOf('nointro') >= 0) S.state.settings.onboarded = true;
    if (!S.state.settings.onboarded) showOnboard();
  }

  init();
})(window);
