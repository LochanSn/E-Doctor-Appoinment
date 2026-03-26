import { useEffect, useState } from "react";
import api from "../api";
import { useAuth } from "../context/AuthContext";

const emptyBookingForm = {
  hotelId: "",
  checkInDate: "",
  checkOutDate: "",
  guests: 1,
  services: [],
  specialRequest: "",
};

const serviceOptions = [
  { value: "BREAKFAST", label: "Breakfast ($15)" },
  { value: "AIRPORT_PICKUP", label: "Airport Pickup ($30)" },
  { value: "SPA_ACCESS", label: "Spa Access ($40)" },
  { value: "CITY_TOUR", label: "City Tour ($25)" },
];

export default function HotelsPage() {
  const { logout, name } = useAuth();
  const [hotels, setHotels] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [bookingForm, setBookingForm] = useState(emptyBookingForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const loadHotels = async () => {
    const { data } = await api.get("/hotels");
    setHotels(data);
  };

  const loadBookings = async () => {
    const { data } = await api.get("/bookings/my");
    setBookings(data);
  };

  useEffect(() => {
    loadHotels();
    loadBookings();
  }, []);

  const toggleService = (service) => {
    setBookingForm((prev) => ({
      ...prev,
      services: prev.services.includes(service)
        ? prev.services.filter((item) => item !== service)
        : [...prev.services, service],
    }));
  };

  const submitBooking = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await api.post("/bookings", {
        hotelId: Number(bookingForm.hotelId),
        checkInDate: bookingForm.checkInDate,
        checkOutDate: bookingForm.checkOutDate,
        guests: Number(bookingForm.guests),
        services: bookingForm.services,
        specialRequest: bookingForm.specialRequest || null,
      });

      setBookingForm(emptyBookingForm);
      setMessage("Booked successfully. Room number assigned.");
      await loadBookings();
    } catch (err) {
      setError(err.response?.data?.message || "Unable to create booking");
    }
  };

  const cancelBooking = async (id) => {
    await api.put(`/bookings/${id}/cancel`);
    await loadBookings();
  };

  return (
    <div className="page">
      <div className="layout">
        <div className="card stack">
          <div className="row">
            <h1>Customer</h1>
            <button onClick={logout}>Logout</button>
          </div>
          <p className="hint">Welcome {name || "Customer"}</p>

          <h2>Available Hotels</h2>
          <div className="stack">
            {hotels.map((hotel) => (
              <div key={hotel.id} className="hotelItem">
                <div>
                  <strong>{hotel.name}</strong>
                  <p>{hotel.city} | {hotel.address} | ${hotel.pricePerNight}</p>
                </div>
                <button onClick={() => setBookingForm((prev) => ({ ...prev, hotelId: String(hotel.id) }))}>Book</button>
              </div>
            ))}
          </div>
        </div>

        <div className="card stack">
          <h2>Book Services</h2>
          <form onSubmit={submitBooking} className="stack">
            <select value={bookingForm.hotelId} onChange={(e) => setBookingForm({ ...bookingForm, hotelId: e.target.value })} required>
              <option value="">Select Hotel</option>
              {hotels.map((hotel) => (
                <option key={hotel.id} value={hotel.id}>{hotel.name} - {hotel.city}</option>
              ))}
            </select>
            <div className="split">
              <input type="date" value={bookingForm.checkInDate} onChange={(e) => setBookingForm({ ...bookingForm, checkInDate: e.target.value })} required />
              <input type="date" value={bookingForm.checkOutDate} onChange={(e) => setBookingForm({ ...bookingForm, checkOutDate: e.target.value })} required />
            </div>
            <input type="number" min="1" placeholder="Guests" value={bookingForm.guests} onChange={(e) => setBookingForm({ ...bookingForm, guests: e.target.value })} required />

            <div className="serviceGrid">
              {serviceOptions.map((service) => (
                <label key={service.value} className="serviceChoice">
                  <input
                    type="checkbox"
                    checked={bookingForm.services.includes(service.value)}
                    onChange={() => toggleService(service.value)}
                  />
                  <span>{service.label}</span>
                </label>
              ))}
            </div>

            <textarea rows="3" placeholder="Special request" value={bookingForm.specialRequest} onChange={(e) => setBookingForm({ ...bookingForm, specialRequest: e.target.value })} />
            <button type="submit">Confirm Booking</button>
          </form>
          {message ? <p className="ok">{message}</p> : null}
          {error ? <p className="error">{error}</p> : null}

          <h2>My Bookings</h2>
          <div className="stack">
            {bookings.map((booking) => (
              <div key={booking.id} className="bookingItem">
                <div>
                  <strong>{booking.hotelName}</strong>
                  <p>Room: {booking.roomNumber} | Floor: {booking.floorNumber}</p>
                  <p>{booking.checkInDate} to {booking.checkOutDate} | Guests: {booking.guests}</p>
                  <p>Status: <span className={booking.status === "CANCELLED" ? "statusCancel" : "statusOk"}>{booking.status}</span></p>
                </div>
                {booking.status !== "CANCELLED" ? <button className="danger" onClick={() => cancelBooking(booking.id)}>Cancel</button> : null}
              </div>
            ))}
            {!bookings.length ? <p>No bookings yet.</p> : null}
          </div>
        </div>
      </div>
    </div>
  );
}