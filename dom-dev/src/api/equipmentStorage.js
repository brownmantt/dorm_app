import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getEquipmentStorages(params) {
  return request.get('/equipment-storages', { params: buildQueryParams(params) })
}

/** @param {string} equipmentAssetId */
export function getEquipmentStoragesByAsset(equipmentAssetId) {
  return request.get(`/equipment-storages/by-asset/${equipmentAssetId}`)
}

/** @param {string} equipmentAssetId @param {object} data */
export function saveEquipmentStoragesByAsset(equipmentAssetId, data) {
  return request.put(`/equipment-storages/by-asset/${equipmentAssetId}`, data)
}

/** @param {string} storageId */
export function deleteEquipmentStorage(storageId) {
  return request.delete(`/equipment-storages/${storageId}`)
}
