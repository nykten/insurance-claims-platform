import { Component, input } from '@angular/core';

export interface StatusEvent { fromStatus?: string; toStatus: string; changedAt?: string; note?: string; }

@Component({selector:'app-status-timeline',standalone:true,template:`<ol class="timeline">@for (event of events(); track $index) {<li><span class="dot"></span><div><strong>{{event.toStatus}}</strong><small>{{event.changedAt}}</small>@if (event.note) {<p>{{event.note}}</p>}</div></li>} </ol>`,styles:[`.timeline{list-style:none;margin:0;padding:0}.timeline li{display:flex;gap:1rem;padding:0 0 1.25rem;position:relative}.timeline li:not(:last-child):after{content:'';position:absolute;left:5px;top:14px;bottom:0;border-left:2px solid #dbe3f1}.dot{width:12px;height:12px;background:#1456d9;border-radius:50%;margin-top:4px;z-index:1}small{display:block;color:#71809a;margin-top:.25rem}p{margin:.4rem 0;color:#52627e}`]})
export class StatusTimelineComponent { events = input.required<StatusEvent[]>(); }
