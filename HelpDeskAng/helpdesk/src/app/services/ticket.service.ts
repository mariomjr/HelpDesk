import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Ticket } from '../model/ticket.model';
import {HELP_DESK_API} from './helpdesk.api'

@Injectable({
  providedIn: 'root'
})
export class TicketService {

  constructor(private http:HttpClient) { }

  createOrUpdate(ticket:Ticket){
    if(ticket.id != null && ticket.id != ''){
      return this.http.put(`${HELP_DESK_API}/api/ticket`,ticket);
    }else{
      ticket.id = null;
      ticket.status = 'NEW';
      return this.http.post(`${HELP_DESK_API}/api/ticket`,ticket);
    }
  }

  findAll(page:number, count:number){
    return this.http.get(`${HELP_DESK_API}/api/ticket/${page}/${count}`);
  }

  findById(id:string){
    return this.http.get(`${HELP_DESK_API}/api/ticket/${id}`);
  }

  delete(id:string){
    return this.http.delete(`${HELP_DESK_API}/api/ticket/${id}`);
  }

  findByParams(page:number, count:number, assignedToMe:boolean,t:Ticket){
    t.number = t.number == null ? 0:t.number;
    t.title = t.title == ''?'uninformed' : t.title;
    t.status = t.status == ''?'uninformed' : t.status;
    t.priority = t.priority == ''?'uninformed' : t.priority;

    return this.http.get(`${HELP_DESK_API}/api/ticket/${page}/${count}/${t.number}/${t.title}/${t.status}/${t.priority}/${assignedToMe}/`);
  }

  changeStatus(status:string,ticket:Ticket){
    return this.http.put(`${HELP_DESK_API}/api/ticket/${ticket.id}/${status}`,ticket);
  }

  summary(){
    return this.http.get(`${HELP_DESK_API}/api/ticket/summary`);
  }
}
