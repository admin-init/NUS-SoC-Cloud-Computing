import { apiOperations, apiOrder } from './index.js'

// 车票相关API
export const ticketAPI = {
  // 搜索车票
  searchTickets(searchParams) {
    console.log('Searching tickets with params:', searchParams)
    return apiOperations.get(`/opt/schedules/search?departureTime=${searchParams.date}&endStationId=${searchParams.stopPos}&startStationId=${searchParams.startPos}`)
  },
  
  // 预订车票
  bookTicket(ticketData) {
    return apiOrder.post(`/orders/1/create?ticketId=${ticketData.ticketId}`, ticketData)
  },
}