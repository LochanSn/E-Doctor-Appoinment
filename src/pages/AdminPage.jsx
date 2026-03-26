import { useEffect, useState } from "react";
import api from "../api";
import { useAuth } from "../context/AuthContext";

const statusTabs = ["ALL", "AVAILABLE", "OCCUPIED", "RESERVED", "MAINTENANCE"];
const emptyHotelForm = { name: "", city: "", address: "", pricePerNight: "" };
const emptyRoomForm = {
  hotelId: "",
  roomNumber: "",
  floorNumber: "",
  minCapacity: "",
  maxCapacity: "",
  status: "AVAILABLE",
};

export default function AdminPage() {
  const { logout, name } = useAuth();
  const [activeTab, setActiveTab] = useState("ALL");
  const [summary, setSummary] = useState({ all: 0, available: 0, occupied: 0, reserved: 0, maintenance: 0 });
  const [hotels, setHotels] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [hotelForm, setHotelForm] = useState(emptyHotelForm);
  const [roomForm, setRoomForm] = useState(emptyRoomForm);
  const [editingHotelId, setEditingHotelId] = useState(null);

  const loadHotels = async () => {
    const { data } = await api.get("/hotels");
    setHotels(data);
  };

  const loadSummary = async () => {
    const { data } = await api.get("/admin/dashboard");
    setSummary(data);
  };

  const loadRooms = async (tab = activeTab) => {
    const url = tab === "ALL" ? "/admin/rooms" : `/admin/rooms?status=${tab}`;
    const { data } = await api.get(url);
    setRooms(data);
  };

  useEffect(() => {
    loadHotels();
    loadSummary();
    loadRooms("ALL");
  }, []);

  const changeTab = async (tab) => {
    setActiveTab(tab);
    await loadRooms(tab);
  };

  const saveHotel = async (e) => {
    e.preventDefault();
    const payload = { ...hotelForm, pricePerNight: Number(hotelForm.pricePerNight) };

    if (editingHotelId) {
      await api.put(`/admin/hotels/${editingHotelId}`, payload);
    } else {
      await api.post("/admin/hotels", payload);
    }

    setHotelForm(emptyHotelForm);
    setEditingHotelId(null);
    await loadHotels();
  };

  const editHotel = (hotel) => {
    setEditingHotelId(hotel.id);
    setHotelForm({
      name: hotel.name,
      city: hotel.city,
      address: hotel.address,
      pricePerNight: String(hotel.pricePerNight),
    });
  };

  const saveRoom = async (e) => {
    e.preventDefault();
    await api.post("/admin/rooms", {
      hotelId: Number(roomForm.hotelId),
      roomNumber: roomForm.roomNumber,
      floorNumber: Number(roomForm.floorNumber),
      minCapacity: Number(roomForm.minCapacity),
      maxCapacity: Number(roomForm.maxCapacity),
      status: roomForm.status,
    });

    setRoomForm(emptyRoomForm);
    await loadSummary();
    await loadRooms(activeTab);
  };

  const updateRoomStatus = async (roomId, status) => {
    await api.put(`/admin/rooms/${roomId}/status`, { status });
    await loadSummary();
    await loadRooms(activeTab);
  };

  return (
    <div className="page">
      <div className="layout adminLayout">
        <div className="card stack">
          <div className="row">
            <h1>Administrator</h1>
            <button onClick={logout}>Logout</button>
          </div>
          <p className="hint">Welcome {name || "Owner"}</p>

          <div className="tabBar">
            {statusTabs.map((tab) => (
              <button
                key={tab}
                className={activeTab === tab ? "tabActive" : "tabBtn"}
                onClick={() => changeTab(tab)}
              >
                {tab.toLowerCase()}
              </button>
            ))}
          </div>

          <div className="statGrid">
            <div className="statBox">All: {summary.all}</div>
            <div className="statBox">Available: {summary.available}</div>
            <div className="statBox">Occupied: {summary.occupied}</div>
            <div className="statBox">Reserved: {summary.reserved}</div>
            <div className="statBox">Maintenance: {summary.maintenance}</div>
          </div>

          <h2>Rooms ({activeTab.toLowerCase()})</h2>
          <div className="stack">
            {rooms.map((room) => (
              <div key={room.id} className="bookingItem">
                <div>
                  <strong>{room.hotelName} - Room {room.roomNumber}</strong>
                  <p>Floor {room.floorNumber} | Capacity {room.minCapacity}-{room.maxCapacity}</p>
                  <p>Status: <span className="statusOk">{room.status}</span></p>
                </div>
                <select value={room.status} onChange={(e) => updateRoomStatus(room.id, e.target.value)}>
                  <option value="AVAILABLE">AVAILABLE</option>
                  <option value="OCCUPIED">OCCUPIED</option>
                  <option value="RESERVED">RESERVED</option>
                  <option value="MAINTENANCE">MAINTENANCE</option>
                </select>
              </div>
            ))}
          </div>
        </div>

        <div className="card stack">
          <h2>Add / Edit Hotel</h2>
          <form className="stack" onSubmit={saveHotel}>
            <input placeholder="Hotel name" value={hotelForm.name} onChange={(e) => setHotelForm({ ...hotelForm, name: e.target.value })} required />
            <input placeholder="City" value={hotelForm.city} onChange={(e) => setHotelForm({ ...hotelForm, city: e.target.value })} required />
            <input placeholder="Address" value={hotelForm.address} onChange={(e) => setHotelForm({ ...hotelForm, address: e.target.value })} required />
            <input type="number" placeholder="Price per night" value={hotelForm.pricePerNight} onChange={(e) => setHotelForm({ ...hotelForm, pricePerNight: e.target.value })} required />
            <button type="submit">{editingHotelId ? "Update Hotel" : "Add Hotel"}</button>
          </form>

          <div className="stack">
            {hotels.map((hotel) => (
              <div key={hotel.id} className="hotelItem">
                <div>
                  <strong>{hotel.name}</strong>
                  <p>{hotel.city} | {hotel.address} | ${hotel.pricePerNight}</p>
                </div>
                <button onClick={() => editHotel(hotel)}>Edit</button>
              </div>
            ))}
          </div>

          <h2>Add Room</h2>
          <form className="stack" onSubmit={saveRoom}>
            <select value={roomForm.hotelId} onChange={(e) => setRoomForm({ ...roomForm, hotelId: e.target.value })} required>
              <option value="">Select Hotel</option>
              {hotels.map((hotel) => (
                <option key={hotel.id} value={hotel.id}>{hotel.name}</option>
              ))}
            </select>
            <div className="split">
              <input placeholder="Room Number" value={roomForm.roomNumber} onChange={(e) => setRoomForm({ ...roomForm, roomNumber: e.target.value })} required />
              <input type="number" min="1" placeholder="Floor" value={roomForm.floorNumber} onChange={(e) => setRoomForm({ ...roomForm, floorNumber: e.target.value })} required />
            </div>
            <div className="split">
              <input type="number" min="1" placeholder="Min Capacity" value={roomForm.minCapacity} onChange={(e) => setRoomForm({ ...roomForm, minCapacity: e.target.value })} required />
              <input type="number" min="1" placeholder="Max Capacity" value={roomForm.maxCapacity} onChange={(e) => setRoomForm({ ...roomForm, maxCapacity: e.target.value })} required />
            </div>
            <select value={roomForm.status} onChange={(e) => setRoomForm({ ...roomForm, status: e.target.value })}>
              <option value="AVAILABLE">AVAILABLE</option>
              <option value="OCCUPIED">OCCUPIED</option>
              <option value="RESERVED">RESERVED</option>
              <option value="MAINTENANCE">MAINTENANCE</option>
            </select>
            <button type="submit">Add Room</button>
          </form>
        </div>
      </div>
    </div>
  );
}