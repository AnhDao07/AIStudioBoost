// ==UserScript==
// @name         AI Studio Boost
// @author      Một Ngàn Nỗi Nhớ - fb.com/troidongconang
// @match        https://aistudio.google.com/*
// @run-at       document-idle
// @grant        none
// ==/UserScript==
(() => {
  'use strict';
  if (window.__aisBoost) return;
  window.__aisBoost = true;

  // ===== CONFIG =====
  const KEEP_LAST = 6;                 // số lượt chat mới nhất luôn hiển thị đầy đủ
  const TURN_SEL = 'ms-chat-turn';     // selector 1 lượt chat (chưa kiểm chứng, sửa nếu AI Studio đổi)
  const INPUT_SEL = 'ms-prompt-box textarea, ms-autosize-textarea textarea, textarea';
  const RUN_SEL = 'ms-run-button button, button[aria-label*="Run" i], button[aria-label*="Send" i]';

  // ===== 1. CSS: bỏ render phần ngoài màn hình =====
  const css = document.createElement('style');
  css.textContent = `
    ${TURN_SEL}{content-visibility:auto;contain-intrinsic-size:auto 500px;}
    .ais-hidden{display:none!important;}
    #ais-more{position:sticky;top:0;z-index:50;margin:6px auto;display:block;padding:6px 14px;
      border:0;border-radius:16px;background:#3c4043;color:#fff;font:13px sans-serif;cursor:pointer;}
    #ais-fab{position:fixed;right:14px;bottom:90px;z-index:99999;width:46px;height:46px;border-radius:50%;
      border:0;background:#1a73e8;color:#fff;font-size:22px;box-shadow:0 2px 8px #0006;}
    #ais-panel{position:fixed;left:8px;right:8px;bottom:8px;z-index:99999;display:none;padding:10px;
      border-radius:14px;background:#202124;box-shadow:0 4px 16px #000a;}
    #ais-panel textarea{width:100%;box-sizing:border-box;height:30vh;background:#303134;color:#fff;
      border:1px solid #5f6368;border-radius:8px;padding:8px;font:15px sans-serif;resize:none;}
    #ais-panel .row{display:flex;gap:8px;margin-top:8px;justify-content:flex-end;}
    #ais-panel button{padding:8px 16px;border:0;border-radius:8px;font:14px sans-serif;}
    #ais-send{background:#1a73e8;color:#fff;} #ais-close{background:#5f6368;color:#fff;}
  `;
  document.documentElement.appendChild(css);

  // ===== 2. Ẩn các lượt chat cũ (DOM nhẹ hơn => gõ/gửi nhanh hơn) =====
  let expanded = false, timer = null;
  function trim() {
    const turns = document.querySelectorAll(TURN_SEL);
    const cut = Math.max(0, turns.length - KEEP_LAST);
    turns.forEach((t, i) => t.classList.toggle('ais-hidden', !expanded && i < cut));
    let btn = document.getElementById('ais-more');
    if (cut > 0 && turns[0]) {
      if (!btn) {
        btn = document.createElement('button');
        btn.id = 'ais-more';
        btn.onclick = () => { expanded = !expanded; trim(); };
        turns[0].parentNode.insertBefore(btn, turns[0]);
      }
      btn.textContent = expanded ? 'Thu gọn lượt cũ' : `Hiện ${cut} lượt cũ (đang ẩn để chạy mượt)`;
    } else if (btn) btn.remove();
  }
  const schedule = () => {
    clearTimeout(timer);
    timer = setTimeout(() => (window.requestIdleCallback || setTimeout)(trim), 600);
  };
  new MutationObserver(schedule).observe(document.body, { childList: true, subtree: true });
  schedule();

  // ===== 3. Ô soạn nhanh: gõ ngoài Angular, bấm Gửi mới đẩy vào ô thật =====
  const fab = document.createElement('button');
  fab.id = 'ais-fab'; fab.textContent = '⚡';
  const panel = document.createElement('div');
  panel.id = 'ais-panel';
  panel.innerHTML = '<textarea placeholder="Gõ ở đây (không lag), bấm Gửi để chuyển vào AI Studio"></textarea>' +
    '<div class="row"><button id="ais-close">Đóng</button><button id="ais-send">Gửi</button></div>';
  document.body.append(fab, panel);
  const ta = panel.querySelector('textarea');
  fab.onclick = () => { panel.style.display = 'block'; ta.focus(); };
  panel.querySelector('#ais-close').onclick = () => (panel.style.display = 'none');

  function realInput() {
    const list = [...document.querySelectorAll(INPUT_SEL)].filter(e => e !== ta && e.offsetParent !== null);
    return list[list.length - 1] || null;
  }
  panel.querySelector('#ais-send').onclick = () => {
    const text = ta.value;
    if (!text.trim()) return;
    const inp = realInput();
    if (!inp) { alert('Không tìm thấy ô nhập của AI Studio (cần cập nhật INPUT_SEL).'); return; }
    // set 1 lần duy nhất bằng native setter để framework nhận giá trị
    const setter = Object.getOwnPropertyDescriptor(HTMLTextAreaElement.prototype, 'value').set;
    setter.call(inp, text);
    inp.dispatchEvent(new Event('input', { bubbles: true }));
    inp.dispatchEvent(new Event('change', { bubbles: true }));
    setTimeout(() => {
      const run = document.querySelector(RUN_SEL);
      if (run && !run.disabled) run.click();
      else inp.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', ctrlKey: true, bubbles: true }));
    }, 250);
    ta.value = '';
    panel.style.display = 'none';
  };
})();
