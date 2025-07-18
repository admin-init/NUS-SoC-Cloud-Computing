import api from './index.js'

// 车票相关API
export const ticketAPI = {
  // 搜索车票
  searchTickets(searchParams) {
    console.log('Searching tickets with params:', searchParams)
    return api.get(`/operation/opt/schedules/search?departureTime=${searchParams.date}&endStationId=${searchParams.stopPos}&startStationId=${searchParams.startPos}`)
  },
  
  // 预订车票
  bookTicket(ticketData) {
    return api.post(`/order/orders/1/create?ticketId=${ticketData.ticketId}`, ticketData)
  },
}